from users.models import User
from posts.models import Post
from django.contrib.auth import get_user_model
from django.db import IntegrityError
from django.db.models import Q
from django.shortcuts import get_object_or_404
from drf_yasg.utils import swagger_auto_schema
from rest_framework import status, viewsets
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from posts.serializers import PostSerializer
from tags.models import Tag

from .models import Follow
from .serializers import UserInfoSerializer, UserPostSerializer, UserSerializer


# ----------------- helper functions for data administration -----------------

def refresh_tags_for_all_users():
    users = User.objects.all()
    for user in users:
        refresh_user_tags_by_user(user)
    print("refresh_tag_for_all_users finished")


def refresh_user_tags_by_user(user):
    print(f'User {user} is trying to refresh their tags')

    # Define the mapping of ratings to weights with a 0.5 interval (actually there is no 0.5 score coming. every weight is on my own..)
    rating_weights = {1: -3.0, 1.5: -2.2, 2: -1.5, 2.5: -0.5, 3: 0.0, 3.5: 0.5, 4: 1.5, 4.5: 2.2, 5: 3.0}

    # Dictionary to store tag weights for each labels (label: weight)
    tag_weighted_sums = {}

    user_with_posts = User.objects.prefetch_related('posts__tags').get(id=user.id)

    # Iterate through user's posts and tags
    for post in user_with_posts.posts.all():
        print(f'checking post id {post.id}: {post.description}, rating: {post.rating}, sentiment: {post.sentiment}')
        for tag in post.tags.all():
            print(f'- checking tag: {tag.ko_label}')
            label = tag.ko_label  # Assuming ko_label is used as the label
            # tag_type = tag.type
            rating = post.rating
            if label not in tag_weighted_sums:
                tag_weighted_sums[label] = 0
            tag_weighted_sums[label] = round(tag_weighted_sums.get(label, 0) + rating_weights[rating], 4)
            if (post.sentiment is not None): #최대 1, 최소 -1이므로 *3
                tag_weighted_sums[label] = round(tag_weighted_sums.get(label, 0) + float(post.sentiment) * 3, 4)

    print("tag_counts", tag_weighted_sums)
    # Dictionary to store the most frequently occurring tag not regarding types
    most_frequent_tags = {}

    # sort the tag_weighted_sums by value
    sorted_tag_weighted_sums = sorted(tag_weighted_sums.items(), key=lambda x: x[1], reverse=True)
    # get top 3 tags with the highest weighted sums, but only use positive weights
    most_frequent_tags = dict(sorted_tag_weighted_sums[:3])
    most_frequent_tags = {k: v for k, v in most_frequent_tags.items() if v > 0}

    print ("most_frequent_tags", most_frequent_tags)

    # Update user's tags with the most frequently occurring tags
    updated_tags = Tag.objects.filter(ko_label__in=most_frequent_tags.keys())
    user.tags.set(updated_tags)


    # get array of post counts of all users

    users_with_posts_count = User.objects.annotate(post_count=Count('posts')).values_list('post_count', flat=True)
    user_posts_count = list(users_with_posts_count)
    print ("user_posts_count", user_posts_count)

    # get array of follower counts of all users

    return Response({"user_tags": [i.ko_label for i in updated_tags]})