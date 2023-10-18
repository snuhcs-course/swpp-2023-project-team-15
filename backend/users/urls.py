from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from .views import register,profile_update,get_my_profile,get_user_profile

urlpatterns = [
    path('register/', register, name='register'),
    path('login/', obtain_auth_token, name='login'),
    path('edit/',profile_update, name='edit'),
    path('me/', get_my_profile, name='my_info'),
    path('<int:pk>/', get_user_profile, name='user_info'),
]
