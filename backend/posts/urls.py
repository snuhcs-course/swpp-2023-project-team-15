from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import PostViewSet

router = DefaultRouter()
router.register('', PostViewSet)

urlpatterns = [
    path('', include(router.urls)),
]
