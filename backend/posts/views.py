import threading

import requests
from decouple import config
from django.db.models import Q
from drf_yasg.utils import swagger_auto_schema
from rest_framework import filters, permissions, status, viewsets
from rest_framework.decorators import action, api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from tags.models import Tag
from tags.utils import deepl_translate_ko_to_en, ml_tagging

from .models import Post
from .serializers import PostSerializer, data_list


def get_top_tags_after_translation(possible_tags, translated_description):
    label_score_dict = ml_tagging(translated_description, possible_tags)
    max_label = max(label_score_dict, key=label_score_dict.get)
    
    if label_score_dict[max_label] > 0.3:
        matching_tag = Tag.objects.filter(en_label=max_label).first()
        return matching_tag
    return None


def create_tags_on_thread(post):
    print("Thread started")
    if len(post.description) < 5:
        print(f'create tag skipped: description too short: {post.description}.')
        return
    translated_description = deepl_translate_ko_to_en(post.description)
    print('translated description', translated_description)
    tags_first_ten = Tag.objects.values('en_label')[:10]
    tags_second_ten = Tag.objects.values('en_label')[10:20]
    tags_third_ten = Tag.objects.values('en_label')[20:30]
    
    for possible_tags_queryset in [tags_first_ten, tags_second_ten, tags_third_ten]:
        possible_tags = [tag['en_label'] for tag in possible_tags_queryset]
        matching_tag = get_top_tags_after_translation(possible_tags, translated_description)
        print('fount tag', matching_tag)
        if matching_tag is not None:
            post.tags.add(matching_tag)
    
    print("Thread finished")


class PostViewSet(viewsets.ModelViewSet):
    queryset = Post.objects.all()
    serializer_class = PostSerializer
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [filters.OrderingFilter]
    ordering_fields = ['created_at']
    ordering = ['-created_at']
    
    def create(self, request):
        serializer = PostSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)        
        # Post 생성 및 저장
        post = serializer.save(user=self.request.user)
        
        thread = threading.Thread(target=create_tags_on_thread, args=(post,), daemon=True)
        thread.start()
                
        return Response(serializer.data, status=status.HTTP_201_CREATED)

        
    def get_queryset(self):
        """
        Optionally restricts the returned purchases to a given user,
        by filtering against a `username` query parameter in the URL.
        """
        queryset = Post.objects.all()
        # Get restaurant_name from query parameters
        restaurant_name = self.request.query_params.get('restaurant_name')
        if restaurant_name:
            queryset = queryset.filter(restaurant__name__icontains=restaurant_name)
            return queryset

        # Get tags from query parameters
        tags_param = self.request.query_params.get('tags')
        if tags_param:
            # Split the tags and create a Q object for each tag
            tags = tags_param.split(',')
            tag_queries = [Q(tags__ko_label=tag) for tag in tags]

            # Combine the Q objects using OR operation
            combined_query = Q()
            for tag_query in tag_queries:
                combined_query |= tag_query

            # Filter the queryset using the combined Q object
            queryset = queryset.filter(combined_query)

        return queryset

    @swagger_auto_schema(
        operation_description="List all posts",
        responses={200: data_list(PostSerializer)}
    )
    def list(self, request, *args, **kwargs):
        queryset = self.get_queryset()
        serializer = self.get_serializer(queryset, many=True, context={"request": request})
        return Response({"data": serializer.data})

    @action(detail=True, methods=['put'], url_path='likes', permission_classes=[permissions.IsAuthenticated], serializer_class=None)
    def like_post(self, request, pk=None):
        post = self.get_object()  # retrieve the post by its pk.
        user = request.user

        # Check if the user already liked this post
        if post.likes.filter(id=user.id).exists():
            # You can decide what to do here, for example, remove the like or simply do nothing
            post.likes.remove(user)  # To unlike the post
            return Response({"message": "Post unliked"}, status=status.HTTP_200_OK)
        else:
            post.likes.add(user)  # This is where the user is added to the likes
            return Response({"message": "Post liked"}, status=status.HTTP_200_OK)

    def get_serializer_context(self):
        """
        Extra context provided to the serializer class.
        """
        context = super(PostViewSet, self).get_serializer_context()
        context.update({
            "request": self.request
        })
        return context
    
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def restaurant_search(request):
    url = "https://dapi.kakao.com/v2/local/search/keyword.json"
    
    query = request.query_params.get('query')
    
    if not query:
        return Response({"message": "`query` parameter is required"}, status=status.HTTP_400_BAD_REQUEST)
    x = request.query_params.get('x')
    y = request.query_params.get('y')
    
    if not x or not y:
        x = "126.938024740159"
        y = "37.4697520000202"        

    querystring = {
        "category_group_code": "FD6,CE7",
        "query": query,
        "x": x,
        "y": y,
        "sort": "distance",
    }

    headers = {"Authorization": f"KakaoAK {config('KAKAO_ACCESS_KEY')}"}

    response = requests.get(url, headers=headers, params=querystring)

    use_keys = ['id', 'place_name', 'road_address_name', 'category_name', 'x', 'y']
    parsed_response = [{k: item[k] for k in use_keys} for item in response.json()['documents']]
    return Response({"data": parsed_response}, status=status.HTTP_200_OK)
