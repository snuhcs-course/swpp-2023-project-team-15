from django.contrib.auth import get_user_model
from django.db import IntegrityError
from django.db.models import Q
from django.shortcuts import get_object_or_404
from drf_yasg.utils import swagger_auto_schema
from rest_framework import status, viewsets
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from posts.serializers import PostSerializer
from tags.models import Tag

from .models import Follow
from .serializers import UserInfoSerializer, UserPostSerializer, UserSerializer

User= get_user_model()



@api_view(['POST'])
def register(request):
    serializer = UserSerializer(data=request.data)
    if not serializer.is_valid():
        required_fields = ['username', 'email', 'password']
        
        # Check if any of the required fields are blank
        blank_errors = [field for field in required_fields if serializer.errors.get(field) == ['This field may not be blank.']]

        if blank_errors:
            return Response({'error': 'All fields are required'}, status=status.HTTP_400_BAD_REQUEST)

        # If not blank errors, return the specific errors for the fields
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    try:
        user = serializer.save()
        token = Token.objects.create(user=user)
        return Response({'token': token.key}, status=status.HTTP_201_CREATED)
    except Exception as e:
        print(e)
        return Response({'error': 'An error occurred while creating the user'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['PATCH'])
@permission_classes([IsAuthenticated])
def profile_update(request):
    try:
        user_instance = request.user
    except User.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    # Check if the user is trying to update their own profile
    if request.user != user_instance:
        return Response(status=status.HTTP_403_FORBIDDEN)
    if request.method == 'PATCH':
        serializer = UserSerializer(user_instance, data=request.data, partial=True, context={'request': request})
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_my_profile(request):
    user = request.user
    # Serialize the user data and return it
    serializer = UserSerializer(user, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_my_liked_posts(request):
    user = request.user
    liked_posts = user.liked_posts.all()
    serializer = PostSerializer(liked_posts, many=True, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_profile(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserSerializer(user, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_posts(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserPostSerializer(user, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def filter_users(request):
    queryset = User.objects.all()
   # Filter by username
    username = request.query_params.get('username')
    if username is not None:
        queryset = queryset.filter(username__icontains=username)

    # Filter by tags
    tags_param = request.query_params.get('tags')
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

    serializer = UserInfoSerializer(queryset, many=True, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def refresh_user_tags(request):
    user = request.user
    print(f'User {user} is trying to refresh their tags')

    # Define the mapping of ratings to weights with a 0.5 interval (actually there is no 0.5 score coming. every weight is on my own..)
    rating_weights = {1: -3.0, 1.5: -2.2, 2: -1.5, 2.5: -0.5, 3: 0.0, 3.5: 0.5, 4: 1.5, 4.5: 2.2, 5: 3.0}

    # Dictionary to store tag weights for each labels (label: weight)
    tag_weighted_sums = {}

    user_with_posts = User.objects.prefetch_related('posts__tags').get(id=user.id)

    # Iterate through user's posts and tags
    for post in user_with_posts.posts.all():
        print(f'checking post id {post.id}: {post.description}, rating: {post.rating}, sentiment: {post.sentiment}')
        for tag in post.tags.all():
            print(f'- checking tag: {tag.ko_label}')
            label = tag.ko_label  # Assuming ko_label is used as the label
            # tag_type = tag.type
            rating = post.rating
            if label not in tag_weighted_sums:
                tag_weighted_sums[label] = 0
            tag_weighted_sums[label] = round(tag_weighted_sums.get(label, 0) + rating_weights[rating], 4)
            if (post.sentiment is not None): #최대 1, 최소 -1이므로 *3
                tag_weighted_sums[label] = round(tag_weighted_sums.get(label, 0) + float(post.sentiment) * 3, 4)

    print("tag_counts", tag_weighted_sums)
    # Dictionary to store the most frequently occurring tag not regarding types
    most_frequent_tags = {}

    # sort the tag_weighted_sums by value
    sorted_tag_weighted_sums = sorted(tag_weighted_sums.items(), key=lambda x: x[1], reverse=True)
    # get top 3 tags with the highest weighted sums, but only use positive weights
    most_frequent_tags = dict(sorted_tag_weighted_sums[:3])
    most_frequent_tags = {k: v for k, v in most_frequent_tags.items() if v > 0}

    print ("most_frequent_tags", most_frequent_tags)

    # Update user's tags with the most frequently occurring tags
    updated_tags = Tag.objects.filter(ko_label__in=most_frequent_tags.keys())
    user.tags.set(updated_tags)

    return Response({"user_tags": [i.ko_label for i in updated_tags]})

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def follow(request, pk):
    user = request.user
    print(f'User {user} is trying to follow user {pk}')

    if user.following.filter(id=pk).exists():
        #TODO
        Follow.objects.filter(follower=user, followee_id=pk).delete()
        following = False
    else:
        #TODO
        try:
            Follow.objects.create(follower=user, followee_id=pk)
            following = True
        except IntegrityError:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    # Serialize the user data and return it
    return Response({"following": following}, status=status.HTTP_200_OK)