from django.db import models
from django.contrib.auth.models import AbstractUser

class User(AbstractUser):
    description = models.TextField(default='')
    #for searching based on tags
   # userTags = models.ManyToManyField('Tag', blank=True)
    avatar_url = models.URLField(default='https://default-avatar.com' )
    follower_count = models.PositiveIntegerField(default=0)
    following_count = models.PositiveIntegerField(default=0)

class Tag(models.Model):
    #implementation may change 
    name= models.CharField(max_length=50, default= '')
