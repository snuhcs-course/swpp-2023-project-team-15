from drf_yasg.utils import swagger_auto_schema
from rest_framework import filters, permissions, status, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response

from .models import Post
from .serializers import PostSerializer, data_list


class PostViewSet(viewsets.ModelViewSet):
    queryset = Post.objects.all()
    serializer_class = PostSerializer
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [filters.OrderingFilter]
    ordering_fields = ['created_at']
    ordering = ['-created_at']

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    @swagger_auto_schema(
        operation_description="List all posts",
        responses={200: data_list(PostSerializer)}
    )
    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())

        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True, context={"request": request})
            return self.get_paginated_response(serializer.data)

        serializer = self.get_serializer(queryset, many=True, context={"request": request})
        return Response({"data": serializer.data})

    @action(detail=True, methods=['put'], url_path='likes', permission_classes=[permissions.IsAuthenticated], serializer_class=None)
    def like_post(self, request, pk=None):
        post = self.get_object()  # retrieve the post by its pk.
        user = request.user

        # Check if the user already liked this post
        if post.likes.filter(id=user.id).exists():
            # You can decide what to do here, for example, remove the like or simply do nothing
            post.likes.remove(user)  # To unlike the post
            return Response({"message": "Post unliked"}, status=status.HTTP_200_OK)
        else:
            post.likes.add(user)  # This is where the user is added to the likes
            return Response({"message": "Post liked"}, status=status.HTTP_200_OK)

    def get_serializer_context(self):
        """
        Extra context provided to the serializer class.
        """
        context = super(PostViewSet, self).get_serializer_context()
        context.update({
            "request": self.request
        })
        return context
