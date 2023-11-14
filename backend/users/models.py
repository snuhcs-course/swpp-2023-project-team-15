from django.db import models
from django.contrib.auth.models import AbstractUser

class User(AbstractUser):
    description = models.TextField(default='')
    avatar_url = models.URLField(default='https://default-avatar.com')
    # Remove follower_count and following_count, as they can be derived from the relationships.
    
    tags = models.ManyToManyField('tags.Tag', related_name='users', blank=True)

    # Many to Many fields for followers and following.
    following = models.ManyToManyField(
        'self', 
        through='Follow', 
        related_name='followers', 
        symmetrical=False, 
        blank=True
    )

class Follow(models.Model):
    follower = models.ForeignKey(User, related_name='following_relation', on_delete=models.CASCADE)
    followee = models.ForeignKey(User, related_name='follower_relation', on_delete=models.CASCADE)
    created = models.DateTimeField(auto_now_add=True)  # Optionally, to track when the follow happened.

    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['follower', 'followee'], name='unique_followers')
        ]

    def __str__(self):
        return f"{self.follower} follows {self.followee}"
