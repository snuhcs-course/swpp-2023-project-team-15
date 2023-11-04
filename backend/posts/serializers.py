from django.contrib.auth import get_user_model
from rest_framework import serializers

from .models import Post, PostPhoto, Restaurant

User= get_user_model()

class RestaurantSerializer(serializers.ModelSerializer):
    class Meta:
        model = Restaurant
        fields = ('name', 'search_id')

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
    tags = serializers.SerializerMethodField()

    class Meta:
        model = Post
        fields = ('id', 'restaurant', 'photos', 'user', 'rating', 'description', 'created_at', 'is_liked', 'like_count','tags')
        read_only_fields = ('user',)

    def create(self, validated_data):
        restaurant_data = validated_data.pop('restaurant')
        photos_data = validated_data.pop('photos', [])
        # Create or get a restaurant based on the name
        restaurant = Restaurant.objects.filter(name=restaurant_data['name']).first()
        if restaurant:
            if 'search_id' in restaurant_data and restaurant_data['search_id'] is not None:
                restaurant.search_id = restaurant_data['search_id']
                restaurant.save()
        else:
            restaurant, created = Restaurant.objects.get_or_create(**restaurant_data)
        
        post = Post.objects.create(restaurant=restaurant, **validated_data)

        for photo_data in photos_data:
            PostPhoto.objects.create(post=post, **photo_data)

        return post

    def get_is_liked(self, obj) -> bool:
        request = self.context.get('request', None)
        if request:
            return obj.likes.filter(id=request.user.id).exists()
        return False
    
    def get_like_count(self, obj) -> int:
        return obj.likes.count()
    
    def get_tags(self, obj):
        tags = obj.tags.all()
        return [f"{tag.ko_label}" for tag in tags]

def data_list(serializer):
    class DataListSerializer(serializers.Serializer):
        data = serializer(many=True)
    
    return DataListSerializer
