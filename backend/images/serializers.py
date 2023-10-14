from rest_framework import serializers


class ImageUploadSerializer(serializers.Serializer):
    image = serializers.ImageField()

class ImageURLResponseSerializer(serializers.Serializer):
    image_url = serializers.URLField()