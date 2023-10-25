from django.contrib.auth import get_user_model
from django.db import IntegrityError
from django.shortcuts import get_object_or_404
from drf_yasg.utils import swagger_auto_schema
from rest_framework import status, viewsets
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from tags.models import Tag

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
def get_user_profile(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserSerializer(user, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
def get_user_posts(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserPostSerializer(user, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)


@api_view(['GET'])
def filter_users(request):
    queryset = User.objects.all()
    username = request.query_params.get('username')
    if username is not None:
        queryset = queryset.filter(username__icontains=username)
    serializer = UserSerializer(queryset, many=True, context={'request': request})
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST'])
def refresh_user_tags(request):
    user = request.user

    # Dictionary to store tag counts for each label type
    tag_counts = {label: {} for label, _ in Tag.TAG_TYPES}

    user_with_posts = User.objects.prefetch_related('posts__tags').get(id=user.id)

    # Iterate through user's posts and tags
    for post in user_with_posts.posts.all():
        for tag in post.tags.all():
            label = tag.ko_label  # Assuming ko_label is used as the label
            tag_type = tag.type

            # Increment tag count for the specific type and label
            tag_counts[tag_type][label] = tag_counts[tag_type].get(label, 0) + 1

    print("tag_counts", tag_counts)
    # Dictionary to store the most frequently occurring tag for each type
    most_frequent_tags = {}

    # Iterate through tag counts for each type
    for tag_type, label_counts in tag_counts.items():
        if label_counts:
            # Find the label with the maximum count
            most_frequent_label = max(label_counts, key=label_counts.get)
            most_frequent_tags[tag_type] = most_frequent_label

    # Update user's tags with the most frequently occurring tags for each type
    updated_tags = Tag.objects.filter(ko_label__in=most_frequent_tags.values())
    user.tags.set(updated_tags)

    return Response({"user_tags": [i.ko_label for i in updated_tags]})