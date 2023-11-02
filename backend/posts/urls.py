from django.urls import include, path
from rest_framework.routers import DefaultRouter

from .views import PostViewSet, restaurant_search

router = DefaultRouter()
router.register('', PostViewSet)

urlpatterns = [
    path('restaurant-search/', restaurant_search, name='restaurant_search'),
    path('', include(router.urls)),
]
