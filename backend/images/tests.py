from io import BytesIO

import boto3
from django.contrib.auth import get_user_model
from django.test import TestCase
from moto import mock_s3
from PIL import Image
from rest_framework import status
from rest_framework.test import APIClient


class ImageUploadViewTest(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.user = get_user_model().objects.create_user(...)  # Replace with user creation details
        self.client.force_authenticate(user=self.user)
        self.image_upload_url = '/images/upload/'  # Replace with your URL

    def generate_photo_file(self):
        """Generate a dummy image file for testing."""
        file = BytesIO()
        image = Image.new('RGB', (100, 100), color='red')
        image.save(file, 'png')
        file.name = 'test.png'
        file.seek(0)
        return file

    @mock_s3
    def test_successful_image_upload(self):
        """Test uploading a valid image."""
        # Setup mock S3
        region = 'ap-northeast-2'
        s3_client = boto3.client('s3', region_name=region)
        s3_client.create_bucket(Bucket='snu-swpp-2023-team-15',
                                CreateBucketConfiguration={'LocationConstraint': region})

        image = self.generate_photo_file()
        response = self.client.post(self.image_upload_url, {
                                'image': image}, format='multipart')
        print(response.data)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn('image_url', response.data)

    def test_invalid_image_data(self):
        """Test uploading invalid image data."""
        response = self.client.post(self.image_upload_url, {'image': 'notanimage'}, format='multipart')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    @mock_s3
    def test_aws_failure(self):
        """Simulate an AWS failure."""
        with mock_s3():
            # Do not create a bucket to simulate the failure
            image = self.generate_photo_file()
            response = self.client.post(self.image_upload_url, {'image': image}, format='multipart')
            self.assertEqual(response.status_code, status.HTTP_500_INTERNAL_SERVER_ERROR)

    def test_unauthenticated_access(self):
        """Test endpoint access without authentication."""
        self.client.force_authenticate(user=None)  # Remove authentication
        image = self.generate_photo_file()
        response = self.client.post(self.image_upload_url, {'image': image}, format='multipart')
        self.assertTrue(response.status_code in [status.HTTP_401_UNAUTHORIZED, status.HTTP_403_FORBIDDEN])

    def tearDown(self):
        # Clean up code (if any)
        pass