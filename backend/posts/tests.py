import datetime
from datetime import timedelta
from unittest.mock import MagicMock, patch

from django.contrib.auth import get_user_model
from django.test import TestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APIClient, APITestCase

from tags.models import Tag
from users.models import Follow, User

from .models import Post, PostPhoto, Restaurant


class PostCreateTestCase(APITestCase):
    def setUp(self):
        # Create a mock user and authenticate
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client = APIClient()
        self.client.force_authenticate(user=self.user)

    @patch('threading.Thread')
    def test_create_post_with_photos(self, mock_thread):
        # Arrange
        mock_thread_instance = MagicMock()
        mock_thread.return_value = mock_thread_instance
        
        post_data = {
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

        # Perform API call to create a post
        url = reverse('post-list')  # 'post-list' should correspond to your url conf for creating posts
        response = self.client.post(url, post_data, format='json')
        
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
        self.assertEqual(photo_instance.photo_url, post_data['photos'][0]['photo_url'])
        self.assertEqual(photo_instance.post, post)

        # Verify Restaurant instance creation
        self.assertEqual(Restaurant.objects.count(), 1)
        restaurant_instance = Restaurant.objects.first()
        self.assertEqual(restaurant_instance.name, post_data['restaurant']['name'])
        self.assertEqual(restaurant_instance.search_id, None)
        self.assertEqual(post.restaurant, restaurant_instance)

    @patch('threading.Thread')
    def test_create_post_with_external_restaurant(self, mock_thread):
        # Arrange
        mock_thread_instance = MagicMock()
        mock_thread.return_value = mock_thread_instance
        post_data = {
            "restaurant": {
                "name": "301동 학식",
                "search_id": "123",
                "category_name": "한식",
            },
            "rating": "3.5",
            "description": "test",
            "photos": [
                {
                    "photo_url": "https://image.ohou.se/i/bucketplace-v2-development/uploads/cards/snapshots/168666382159905764.jpeg?gif=1&w=480&h=480&c=c&q=80&webp=1"
                }
            ]
        }

        # Perform API call to create a post
        url = reverse('post-list')  # 'post-list' should correspond to your url conf for creating posts
        response = self.client.post(url, post_data, format='json')
        
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
        self.assertEqual(photo_instance.photo_url, post_data['photos'][0]['photo_url'])
        self.assertEqual(photo_instance.post, post)

        # Verify Restaurant instance creation
        self.assertEqual(Restaurant.objects.count(), 1)
        restaurant_instance = Restaurant.objects.first()
        self.assertEqual(restaurant_instance.name, post_data['restaurant']['name'])
        self.assertEqual(restaurant_instance.search_id, post_data['restaurant']['search_id'])
        self.assertEqual(restaurant_instance.category_name, post_data['restaurant']['category_name'])
        self.assertEqual(post.restaurant, restaurant_instance)
        
    @patch('threading.Thread')
    def test_create_post_with_duplicate_restaurant(self, mock_thread):
        # Arrange
        mock_thread_instance = MagicMock()
        mock_thread.return_value = mock_thread_instance
        post_data = {
            "restaurant": {
                "name": "301동 학식",
            },
            "rating": "3.5",
            "description": "test",
            "photos": [
                {
                    "photo_url": "https://image.ohou.se/i/bucketplace-v2-development/uploads/cards/snapshots/168666382159905764.jpeg?gif=1&w=480&h=480&c=c&q=80&webp=1"
                }
            ]
        }

        # Perform API call to create a post
        url = reverse('post-list')  # 'post-list' should correspond to your url conf for creating posts
        self.client.post(url, post_data, format='json')
        
        post_data['restaurant']['search_id'] = '123'
        
        response = self.client.post(url, post_data, format='json')
        
        # Verify response
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertIn('id', response.data)
        self.assertIn('photos', response.data)

        
        # Verify Restaurant instance creation
        post = Post.objects.first()
        self.assertEqual(Restaurant.objects.count(), 1)
        restaurant_instance = Restaurant.objects.first()
        self.assertEqual(restaurant_instance.name, post_data['restaurant']['name'])
        self.assertEqual(restaurant_instance.search_id, post_data['restaurant']['search_id'])
        self.assertEqual(post.restaurant, restaurant_instance)
        
    @patch('threading.Thread')
    def test_create_post_with_duplicate_restaurant_2(self, mock_thread):
        # Arrange
        mock_thread_instance = MagicMock()
        mock_thread.return_value = mock_thread_instance
        post_data = {
            "restaurant": {
                "name": "301동 학식",
                "search_id": "123",
            },
            "rating": "3.5",
            "description": "test",
            "photos": [
                {
                    "photo_url": "https://image.ohou.se/i/bucketplace-v2-development/uploads/cards/snapshots/168666382159905764.jpeg?gif=1&w=480&h=480&c=c&q=80&webp=1"
                }
            ]
        }

        # Perform API call to create a post
        url = reverse('post-list')  # 'post-list' should correspond to your url conf for creating posts
        self.client.post(url, post_data, format='json')
        
        del post_data['restaurant']['search_id']
        
        response = self.client.post(url, post_data, format='json')
        
        # Verify response
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertIn('id', response.data)
        self.assertIn('photos', response.data)

        
        # Verify Restaurant instance creation
        post = Post.objects.first()
        self.assertEqual(Restaurant.objects.count(), 1)
        restaurant_instance = Restaurant.objects.first()
        self.assertEqual(restaurant_instance.name, post_data['restaurant']['name'])
        self.assertEqual(restaurant_instance.search_id, "123")
        self.assertEqual(post.restaurant, restaurant_instance)
        
    def tearDown(self):
        # Clean up any objects created
        self.client.logout()
        PostPhoto.objects.all().delete()
        Post.objects.all().delete()
        Tag.objects.all().delete()
        Restaurant.objects.all().delete()
        User.objects.all().delete()


class PostOrderingTestCase(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.user_followed = get_user_model().objects.create_user(
            username='testuser', password='12345')
        
        self.user_unfollowed = get_user_model().objects.create_user(
            username='testuser2', password='12345')
        
        self.user_viewer = get_user_model().objects.create_user(
            username='testuser3', password='12345')
        
        Follow.objects.create(follower=self.user_viewer, followee=self.user_followed)
        
        '''
        Post 1 -> 내가 팔로우하고 있는 유저가 만들었고
        Post 2 -> 그렇지 않음
        
        조회할 때는  -> Post 1만 내려와야 함.
        '''
        
        self.restaurant = Restaurant.objects.create(
            name='Test Restaurant')
        self.tag = Tag.objects.create(ko_label='한식', en_label='Korean', type='from_category')

                
        # add tag to user_viewer
        self.user_viewer.tags.add(self.tag)


        now = datetime.datetime.now()
        # post 1
        post_1 = Post.objects.create(
            user=self.user_followed,
            restaurant=self.restaurant,
            rating=4.5,
            description=f'Test Description Followed',
            created_at=now - timedelta(days=1)
        )

        # post 2
        post_2 = Post.objects.create(
            user=self.user_unfollowed,
            restaurant=self.restaurant,
            rating=4.5,
            description=f'Test Description Unfollowed',
            created_at=now - timedelta(days=2)
        )
        post_2.tags.add(self.tag)

        

    def test_post_following(self):
        self.client.force_authenticate(user=self.user_viewer)
        response = self.client.get('/posts/following/')
        self.assertEqual(response.status_code, 200)
        results = response.json()

        # Check ordering
        self.assertTrue(
            all(results[i]['created_at'] >= results[i + 1]['created_at']
                for i in range(len(results) - 1)),
            "Posts are not ordered in reversed chronological order"
        )
        
        # Check filtering
        self.assertTrue(len(results['data']) == 1)
        self.assertTrue(results['data'][0]['description'] == 'Test Description Followed')
        
    def test_post_recommend(self):
        self.client.force_authenticate(user=self.user_viewer)
        response = self.client.get('/posts/recommend/')
        self.assertEqual(response.status_code, 200)
        results = response.json()

        # Check ordering
        self.assertTrue(
            all(results[i]['like_count'] >= results[i + 1]['like_count']
                for i in range(len(results) - 1)),
            "Posts are not ordered in reverse like order"
        )
        
        # Check filtering
        self.assertTrue(len(results['data']) == 1)
        self.assertTrue(results['data'][0]['description'] == 'Test Description Unfollowed') # post 2