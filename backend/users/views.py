from django.db import IntegrityError
from django.contrib.auth import get_user_model
from rest_framework import status
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view
from rest_framework.response import Response


User= get_user_model()

@api_view(['POST'])
def register(request):
    username = request.data.get('username')
    password = request.data.get('password')
    email = request.data.get('email')
    # Validation: Check if all required fields are provided
    if not username or not password or not email:
        return Response({'error': 'All fields are required'}, status=status.HTTP_400_BAD_REQUEST)

    # Check if the username or email is already taken
    if User.objects.filter(username=username).exists():
        return Response({'error': 'Username is already taken'}, status=status.HTTP_400_BAD_REQUEST)
    if User.objects.filter(email=email).exists():
        return Response({'error': 'Email is already in use'}, status=status.HTTP_400_BAD_REQUEST)
    
    try:
        user = User.objects.create_user(username=username, email=email, password=password)
        token = Token.objects.create(user=user)
        return Response({'token': token.key}, status=status.HTTP_201_CREATED)
    except Exception as e:
        return Response({'error': 'An error occurred while creating the user'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)



