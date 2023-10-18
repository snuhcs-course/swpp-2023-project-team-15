from rest_framework import serializers
from django.contrib.auth import get_user_model
from posts.serializers import PostSerializer
from posts.models import Post
from django.db import models


User= get_user_model()
class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, required=True, style={
                                     'input_type': 'password'})
    posts = PostSerializer(source='post_set', many=True, read_only=True)
    email = models.EmailField(unique=True, blank=False) 

    class Meta:
        model = User
        fields = ('id', 'username', 'password', 'email','description', 
                  'avatar_url', 'follower_count', 'following_count','posts')
        error_messages={
            'username':{'error': 'Username is already taken'},
            'email':{'error': 'Email is already in use'}
        }

    def create(self, validated_data):
        user = User.objects.create_user(
            username=validated_data['username'],
            email=validated_data['email'],
            password=validated_data['password'],
            description= validated_data.get('description', ''),
            avatar_url=validated_data.get('avatar_url', 'https://default_avatar-url.com'),
            follower_count=validated_data.get('follower_count', 0),
            following_count=validated_data.get('following_count', 0)

        )
        return user
    #frontend needs to send @PATCH with only description and data
    def update(self, instance, validated_data):
        instance.avatar_url = validated_data.get('avatar_url', instance.avatar_url)
        instance.description = validated_data.get('description', instance.description)
        instance.save()
        return instance
        

    def validate_avatar_url(self, value):
        if not value:
            return "https://default_avatar-url.com"
        return value
    
class UserPostSerializer(serializers.ModelSerializer):
    posts = PostSerializer(source='post_set', many=True, read_only=True)
    
    class Meta:
        model = Post
        fields=('posts',)

class UserInfoSerializer(serializers.ModelSerializer):
    class Meta:
        model=User
        fields=('username', 'avatar_url', 'description')
