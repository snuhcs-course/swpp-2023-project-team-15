from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from .views import register,profile_update

urlpatterns = [
    path('register/', register, name='register'),
    path('login/', obtain_auth_token, name='login'),
    path('edit/',profile_update, name='edit')
]
