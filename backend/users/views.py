from django.contrib.auth.models import User
from rest_framework import status
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view
from rest_framework.response import Response


@api_view(['POST'])
def register(request):
    username = request.data.get('username')
    password = request.data.get('password')

    user = User.objects.create_user(username=username, password=password)
    token = Token.objects.create(user=user)

    return Response({'token': token.key}, status=status.HTTP_201_CREATED)
