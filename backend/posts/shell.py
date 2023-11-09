import requests
from decouple import config
from django.db.models import Q
from drf_yasg.utils import swagger_auto_schema
from rest_framework import filters, permissions, status, viewsets
from rest_framework.decorators import action, api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from tags.models import Tag
from tags.utils import (category_name_to_tags, google_translate_ko_to_en,
                        ml_sentiment_analysis, ml_tagging)

from .models import Post
from .serializers import PostSerializer, data_list

from posts.views import create_tags_on_thread

# ----------------- helper functions for data administration -----------------
# for all posts, clear tags and sentiment, and create tags and sentiment again
def create_tags_on_all_posts():
    print("Re-Create started")
    # calculate category tags
    posts = Post.objects.all()
    for post in posts:
        post.tags.clear()
        post.sentiment = 0.0
        create_tags_on_thread(post)
    print("Re-Create finished")

def create_tags_on_post_id(id):
    post = Post.objects.get(id=id)
    create_tags_on_thread(post)