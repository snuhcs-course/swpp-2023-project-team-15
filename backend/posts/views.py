import threading

import requests
from decouple import config
from django.db.models import Count, Q
from drf_yasg.utils import swagger_auto_schema
from rest_framework import filters, permissions, status, viewsets
from rest_framework.decorators import action, api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from posts.utils import create_tags_on_thread
from tags.models import Tag
from tags.utils import (category_name_to_tags, google_translate_ko_to_en,
                        ml_sentiment_analysis, ml_tagging)

from .models import Post
from .serializers import PostSerializer, data_list


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

        return queryset
    

    def get_queryset_recommend(self):
        # get request user's tags
        user = self.request.user
        user_tags = user.tags.all()

        queryset = Post.objects.all().exclude(user=user)

        # Counting intersecting tags and like_count annotating it
        queryset = queryset.annotate(
            intersecting_tags_count=Count('tags', filter=Q(tags__in=user_tags), distinct=True),
            like_count=Count('likes', distinct=True)
        )


        # Filtering out posts with no intersecting tags and zero like_count
        queryset = queryset.filter(
            Q(intersecting_tags_count__gt=0) | Q(like_count__gt=0)
        )

        # Ordering by intersecting tags count and like_count
        queryset = queryset.order_by('-intersecting_tags_count', '-like_count')

        return queryset

    @swagger_auto_schema(
        operation_description="List all posts",
        responses={200: data_list(PostSerializer)}
    )
    def list(self, request, *args, **kwargs):
        queryset = self.get_queryset()
        serializer = self.get_serializer(queryset, many=True, context={"request": request})
        return Response({"data": serializer.data})
    
    @action(detail=False, methods=['get'], url_path='following', permission_classes=[permissions.IsAuthenticated], serializer_class=PostSerializer)
    def list_following(self, request):
        user = request.user
        following = user.following.all()
        posts = Post.objects.filter(user__in=following)
        serializer = self.get_serializer(posts, many=True, context={"request": request})
        return Response({"data": serializer.data})
    
    '''
    select * from posts; -> Post.objects.all()
    select * from posts where id = 1 -> Post.objects.filter(id=1)
    '''

    @action(detail=False, methods=['get'], url_path='recommend', permission_classes=[permissions.IsAuthenticated], serializer_class=PostSerializer)
    def list_recommend(self, request):
        queryset = self.get_queryset_recommend()
        serializer = self.get_serializer(
            queryset, many=True, context={"request": request})
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
    print (response.json())
    parsed_response = [{k: item[k] for k in use_keys} for item in response.json()['documents']]
    return Response({"data": parsed_response}, status=status.HTTP_200_OK)


