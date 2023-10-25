from django.contrib.auth.models import AbstractUser
from django.db import models


class User(AbstractUser):
    description = models.TextField(default='')
    avatar_url = models.URLField(default='https://default-avatar.com' )
    follower_count = models.PositiveIntegerField(default=0)
    following_count = models.PositiveIntegerField(default=0)
    
    tags = models.ManyToManyField('tags.Tag', related_name='users', blank=True)

