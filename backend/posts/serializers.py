from rest_framework import serializers

from .models import Post, PostPhoto, Restaurant


class RestaurantSerializer(serializers.ModelSerializer):
    class Meta:
        model = Restaurant
        fields = '__all__'

class PostPhotoSerializer(serializers.ModelSerializer):
    class Meta:
        model = PostPhoto
        fields = '__all__'
        read_only_fields = ('post',)


class PostSerializer(serializers.ModelSerializer):
    restaurant = RestaurantSerializer()
    photos = PostPhotoSerializer(many=True, required=False)

    class Meta:
        model = Post
        fields = '__all__'
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
    
