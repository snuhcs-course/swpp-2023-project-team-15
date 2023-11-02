from unittest.mock import MagicMock, patch

from django.urls import reverse
from rest_framework import status
from rest_framework.test import APIClient, APITestCase

from tags.models import Tag
from users.models import User

from .models import Post, PostPhoto, Restaurant


class PostCreateTestCase(APITestCase):
    def setUp(self):
        # Create a mock user and authenticate
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client = APIClient()
        self.client.force_authenticate(user=self.user)
        
        # Prepare test data for post creation
        self.post_data = {
            "restaurant": {
                "name": "301동 학식"
            },
            "rating": "3.5",
            "description": "test",
            "photos": [
                {
                    "photo_url": "https://image.ohou.se/i/bucketplace-v2-development/uploads/cards/snapshots/168666382159905764.jpeg?gif=1&w=480&h=480&c=c&q=80&webp=1"
                }
            ]
        }

    @patch('threading.Thread')
    def test_create_post_with_photos(self, mock_thread):
        # Arrange
        mock_thread_instance = MagicMock()
        mock_thread.return_value = mock_thread_instance

        # Perform API call to create a post
        url = reverse('post-list')  # 'post-list' should correspond to your url conf for creating posts
        response = self.client.post(url, self.post_data, format='json')
        
        # Verify response
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertIn('id', response.data)
        self.assertIn('photos', response.data)

        # Verify Post instance creation
        self.assertEqual(Post.objects.count(), 1)
        post = Post.objects.first()
        print(post)
        self.assertEqual(post.description, 'test')
        self.assertEqual(post.rating, 3.5)  # Ensure the rating is correctly processed as a float

        # Verify PostPhoto instance creation
        self.assertEqual(PostPhoto.objects.count(), 1)
        photo_instance = PostPhoto.objects.first()
        self.assertEqual(photo_instance.photo_url, self.post_data['photos'][0]['photo_url'])
        self.assertEqual(photo_instance.post, post)

        # Verify tag association logic if applicable (omitted for brevity)
        
    def tearDown(self):
        # Clean up any objects created
        self.client.logout()
        PostPhoto.objects.all().delete()
        Post.objects.all().delete()
        Tag.objects.all().delete()
        Restaurant.objects.all().delete()
        User.objects.all().delete()
