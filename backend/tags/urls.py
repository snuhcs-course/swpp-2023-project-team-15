from django.urls import path

from .views import *

urlpatterns = [
    path('top-tags/', get_user_top_tags, name='get_user_top_tags'),
]
