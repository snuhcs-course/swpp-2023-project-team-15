from unittest.mock import patch

from django.contrib.auth import get_user_model
from django.test import TestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APIClient, APITestCase

from posts.models import Post, Restaurant
from tags.models import Tag


class UserTests(APITestCase):
    def test_create_user_and_update(self):
        """
        Create New User and Edit
        """
        # Do Create
        url = reverse('register')
        data = {
            'username': 'testuser',
            'email': 'test@test.com',
            'password': 'testpassword'
        }
        response = self.client.post(url, format='json', data=data)
        print(response.data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertIsNotNone(response.data['token'])
        
        # Do Edit
        url_update = reverse('edit')
        data_update = {
            'description': 'test description',
        }
        
        self.client.force_authenticate(user= get_user_model().objects.get(username='testuser'))
        response_edit = self.client.patch(url_update, format='json', data=data_update)
        
        self.assertEqual(response_edit.status_code, status.HTTP_200_OK)
        self.assertEqual(response_edit.data['description'], 'test description')
        
    def test_create_user_not_valid(self):
        """
        Create New User and Edit
        """
        # Do Create
        url = reverse('register')
        data = {
            'username': 'testuser',
            'email': '1234',
            'password': 'testpassword'
        }
        
        response = self.client.post(url, format='json', data=data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        


class RefreshUserTagsTest(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.user = get_user_model().objects.create_user(username='testuser')
        self.client.force_authenticate(user=self.user)

        # Create test restaurants
        restaurant1 = Restaurant.objects.create(name='Test Restaurant 1', search_id='001', category_name='Korean')
        restaurant2 = Restaurant.objects.create(name='Test Restaurant 2', search_id='002', category_name='Italian')

        # Create test tags
        tag1 = Tag.objects.create(ko_label='한식', en_label='Korean', type='from_category')
        tag2 = Tag.objects.create(ko_label='이탈리안', en_label='Italian', type='from_category')
        
        tag_review_king = Tag.objects.create(ko_label='리뷰왕', en_label='Review King', type='user_stat')
        tag_influencer = Tag.objects.create(ko_label='인플루언서', en_label='Influencer', type='user_stat')
        tag_review_angel = Tag.objects.create(ko_label='리뷰천사', en_label='Review Angel', type='user_stat')
        tag_gourmet = Tag.objects.create(ko_label='고든램지', en_label='Gourmet', type='user_stat')

        # Create test posts with ratings and sentiments
        post1 = Post.objects.create(user=self.user, restaurant=restaurant1, rating=4.5, description='Great food!', sentiment=0.8)
        post1.tags.add(tag1)
        post2 = Post.objects.create(user=self.user, restaurant=restaurant2, rating=2.0, description='bad food!', sentiment=-0.3)
        post2.tags.add(tag2)

    def test_refresh_user_tags(self):
        # Make the POST request to refresh user tags
        response = self.client.post(reverse('refresh_user_tags'))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        
        # Check the response data
        expected_tags = ['한식', '리뷰왕', '인플루언서', '리뷰천사', '고든램지']
        self.assertListEqual(sorted(response.data['user_tags']), sorted(expected_tags))
        self.assertListEqual(sorted(response.data['added']), sorted(expected_tags))
        self.assertListEqual(response.data['removed'], [])

    def tearDown(self):
        pass
        # Clean up code (if any)

class UserFollowTest(TestCase):
    def setUp(self):
        self.client = APIClient()
        self.user = get_user_model().objects.create_user(username='testuser')
        self.user2 = get_user_model().objects.create_user(username='testuser2')
            
        self.client.force_authenticate(user=self.user)

    def test_user_follow(self):
        # Make the POST request to follow user
        response = self.client.post(
            reverse('follow', kwargs={'pk': self.user2.id}))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['following'], True)
        
        response = self.client.post(
            reverse('follow', kwargs={'pk': self.user2.id}))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['following'], False)
        
        