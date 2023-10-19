from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from .views import *

urlpatterns = [
    path('register/', register, name='register'),
    path('login/', obtain_auth_token, name='login'),
    path('edit/',profile_update, name='edit'),
    path('me/', get_my_profile, name='my_profile'),
    path('<int:pk>/', get_user_profile, name='user_profile'),
    path('posts/<int:pk>/',get_user_posts, name='user_posts'),
    path('info/<int:pk>/',get_user_info, name='user_info'),
]
