from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status
from .models import Tag
from .serializers import TagSerializer
from django.db.models import Count

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_user_top_tags(request):
    # logic to retrieve the top 10 tags for all users
    top_tags = Tag.objects.annotate(user_count=Count('users')).order_by('-user_count')[:10]

    # print the user count for each tag
    # for tag in top_tags:
    #     print(f"Tag: {tag.ko_label}, User Count: {tag.user_count}")

    serializer = TagSerializer(top_tags, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

