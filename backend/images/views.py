# views.py

from rest_framework.views import APIView
from rest_framework.parsers import MultiPartParser
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated
import boto3

class ImageUploadView(APIView):
    parser_classes = (MultiPartParser,)
    permission_classes = [IsAuthenticated]

    def post(self, request):
        image = request.data.get('image')

        if not image:
            return Response({"error": "No image provided"}, status=status.HTTP_400_BAD_REQUEST)

        # Upload to S3
        s3 = boto3.client('s3')
        bucket_name = 'snu-swpp-2023-team-15'

        try:
            s3.upload_fileobj(image, bucket_name, image.name)
            return Response({"message": "Upload successful"}, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

