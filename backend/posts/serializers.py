from django.contrib.auth import get_user_model
from rest_framework import serializers

from .models import Post, PostPhoto, Restaurant

User= get_user_model()

class RestaurantSerializer(serializers.ModelSerializer):
    class Meta:
        model = Restaurant
        fields = '__all__'

class PostPhotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = PostPhoto
        fields = '__all__'
        read_only_fields = ('post',)

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('username', 'id', 'avatar_url', 'description')
        read_only_fields = ('username', 'id', 'avatar_url','description')

class PostSerializer(serializers.ModelSerializer):
    restaurant = RestaurantSerializer()
    photos = PostPhotoSerializer(many=True, required=False)
    user = UserSerializer(read_only=True)
    is_liked = serializers.SerializerMethodField()
    like_count = serializers.SerializerMethodField()
    class Meta:
        model = Post
        fields = ('id', 'restaurant', 'photos', 'user', 'rating', 'description', 'created_at', 'is_liked', 'like_count')
        read_only_fields = ('user',)

    def create(self, validated_data):
        restaurant_data = validated_data.pop('restaurant')
        photos_data = validated_data.pop('photos', [])
        # Create or get a restaurant based on the name
        restaurant, created = Restaurant.objects.get_or_create(**restaurant_data)
        
        post = Post.objects.create(restaurant=restaurant, **validated_data)

        for photo_data in photos_data:
            PostPhoto.objects.create(post=post, **photo_data)

        return post

    def get_is_liked(self, obj):
        request = self.context.get('request', None)
        if request:
            return obj.likes.filter(id=request.user.id).exists()
        return False
    
    def get_like_count(self, obj):
        return obj.likes.count()

def data_list(serializer):
    class DataListSerializer(serializers.Serializer):
        data = serializer(many=True)
    
    return DataListSerializer
