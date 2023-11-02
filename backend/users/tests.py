from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase


class UserTests(APITestCase):
    def test_create_user(self):
        """
        Create New User.
        """
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