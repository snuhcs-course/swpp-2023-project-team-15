from django.db import IntegrityError
from django.contrib.auth import get_user_model
from rest_framework import status, viewsets
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from .serializers import UserSerializer,UserPostSerializer, UserInfoSerializer
from django.shortcuts import get_object_or_404
from drf_yasg.utils import swagger_auto_schema


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

#@swagger_auto_schema(request_body=UserSerializer, responses={200: Response})
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
        serializer = UserSerializer(user_instance, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

#@swagger_auto_schema(request_body=UserSerializer, responses={200: Response})   
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_my_profile(request):
    user = request.user
    # Serialize the user data and return it
    serializer = UserSerializer(user)
    return Response(serializer.data, status=status.HTTP_200_OK)

#@swagger_auto_schema(request_body=UserSerializer, responses={200: Response})
@api_view(['GET'])
def get_user_profile(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserSerializer(user)
    return Response(serializer.data, status=status.HTTP_200_OK)

#@swagger_auto_schema(request_body=UserPostSerializer, responses={200: Response})
@api_view(['GET'])
def get_user_posts(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserPostSerializer(user)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET'])
def get_user_info(request, pk):
    user = get_object_or_404(User, pk=pk)
    serializer = UserInfoSerializer(user)
    return Response(serializer.data, status=status.HTTP_200_OK)