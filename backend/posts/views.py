from drf_yasg.utils import swagger_auto_schema
from django.db.models import Q
from rest_framework import filters, permissions, status, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from tags.models import Tag
from tags.utils import deepl_translate_ko_to_en, ml_tagging

from .models import Post
from .serializers import PostSerializer, data_list


def get_top_tags_after_translation(possible_tags, translated_description):
    label_score_dict = ml_tagging(translated_description, possible_tags)
    max_label = max(label_score_dict, key=label_score_dict.get)
    
    print(f'가장 높은 스코어를 가진 레이블: {max_label}. 스코어: {label_score_dict[max_label]}')
    if label_score_dict[max_label] > 0.3:
        matching_tag = Tag.objects.filter(en_label=max_label).first()
        return matching_tag
    return None

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

        # ml_tagging 함수를 사용하여 description에 대한 태깅을 수행 
        tags_first_ten = Tag.objects.values('en_label')[:10]
        tags_second_ten = Tag.objects.values('en_label')[10:20]
        tags_third_ten = Tag.objects.values('en_label')[20:30]
        
        # Post 생성 및 저장
        post = serializer.save(user=self.request.user)
        
        translated_description = deepl_translate_ko_to_en(serializer.validated_data['description'])
        for possible_tags_queryset in [tags_first_ten, tags_second_ten, tags_third_ten]:
            possible_tags = [tag['en_label'] for tag in possible_tags_queryset]
            matching_tag = get_top_tags_after_translation(possible_tags, translated_description)
            if matching_tag is not None:
                post.tags.add(matching_tag)            
                
        print(serializer.data)
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
    
    