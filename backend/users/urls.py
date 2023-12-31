from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from .views import *

urlpatterns = [
    path('register/', register, name='register'),
    path('login/', obtain_auth_token, name='login'),
    path('edit/',profile_update, name='edit'),
    path('me/', get_my_profile, name='my_profile'),
    path('me/liked-posts/', get_my_liked_posts, name='my_liked_posts'),
    path('<int:pk>/', get_user_profile, name='user_profile'),
    path('posts/<int:pk>/',get_user_posts, name='user_posts'),
    path('filter/', filter_users, name='filter_users'),
    path('refresh-tags/', refresh_user_tags, name='refresh_user_tags'),
    path('<int:pk>/follow/', follow, name='follow'),
    path('<int:pk>/followers/', follower_list, name='follower_list'),
    path('<int:pk>/followings/', following_list, name="following_list"),
]
