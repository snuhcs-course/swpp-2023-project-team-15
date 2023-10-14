from django.contrib import admin

from .models import Post, PostPhoto, Restaurant


@admin.register(Post)
class PostAdmin(admin.ModelAdmin):
    list_display = ["id", "user", "restaurant_id", "rating", "created_at"]
    search_fields = ["description"]
    list_filter = ["created_at", "rating"]
    ordering = ["-created_at"]


@admin.register(PostPhoto)
class PostPhotoAdmin(admin.ModelAdmin):
    list_display = ["id", "post", "photo_url"]
    search_fields = ["photo_url"]
    list_filter = ["post"]


@admin.register(Restaurant)
class RestaurantAdmin(admin.ModelAdmin):
    list_display = ["id", "name"]
    search_fields = ["name"]
    list_filter = ["name"]
