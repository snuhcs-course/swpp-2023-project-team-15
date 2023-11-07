from django.conf import settings
from django.contrib.auth import get_user_model
from django.db import models


class Restaurant(models.Model):
    name = models.CharField(max_length=255)
    search_id = models.CharField(max_length=255, null=True)
    category_name = models.CharField(max_length=255, null=True)
    



class Post(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='posts')
    restaurant = models.ForeignKey(
        Restaurant,
        on_delete=models.CASCADE,
        db_column="restaurant_id",
        related_name="posts",
    )
    rating = models.DecimalField(max_digits=5, decimal_places=1)
    description = models.TextField()
    
    likes = models.ManyToManyField(get_user_model(), related_name='liked_posts', blank=True)
    
    created_at = models.DateTimeField(auto_now_add=True)
    
    tags = models.ManyToManyField('tags.Tag', related_name='posts', blank=True)

    
    class Meta:
        ordering = ['-created_at']



class PostPhoto(models.Model):
    post = models.ForeignKey(Post, related_name="photos", on_delete=models.CASCADE)
    photo_url = models.URLField(max_length=500)

    def __str__(self):
        return f"Photo for {self.post.id}"
    

