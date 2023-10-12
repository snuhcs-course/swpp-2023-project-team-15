# views.py

import uuid

import boto3
from decouple import config
from drf_yasg.utils import swagger_auto_schema
from rest_framework import status
from rest_framework.parsers import MultiPartParser
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from images.serializers import (ImageUploadSerializer,
                                ImageURLResponseSerializer)


class ImageUploadView(APIView):
    parser_classes = (MultiPartParser,)
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(request_body=ImageUploadSerializer, responses={200: ImageURLResponseSerializer})
    def post(self, request):
        # Using the serializer to validate the data
        serializer = ImageUploadSerializer(data=request.data)
        
        # Check if serialized data is valid
        if not serializer.is_valid():
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

        # Get the validated image from the serializer
        image = serializer.validated_data['image']

        # Upload to S3
        s3 = boto3.client('s3', 
                          aws_access_key_id=config('AWS_ACCESS_KEY_ID'),
                          aws_secret_access_key=config('AWS_SECRET_ACCESS_KEY')
        )
        bucket_name = 'snu-swpp-2023-team-15'

        try:
            image_name = str(uuid.uuid4()) + image.name[image.name.rfind('.'):]
            s3.upload_fileobj(image, bucket_name, image_name, ExtraArgs={'ContentType': image.content_type})
            s3_url = f"https://dxq3o63jjlg0w.cloudfront.net/{image_name}"
            
            # Use the serializer to structure the response
            response_serializer = ImageURLResponseSerializer(data={"image_url": s3_url})
            if response_serializer.is_valid():
                return Response(response_serializer.data, status=status.HTTP_200_OK)
            else:
                # Handle any serialization errors (shouldn't occur in typical scenarios)
                return Response(response_serializer.errors, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)