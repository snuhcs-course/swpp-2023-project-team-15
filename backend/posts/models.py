from django.conf import settings
from django.db import models


class Restaurant(models.Model):
    name = models.CharField(max_length=255)

    def __str__(self):
        return self.name


class Post(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    restaurant = models.ForeignKey(
        Restaurant,
        on_delete=models.CASCADE,
        db_column="restaurant_id",
        related_name="posts",
    )
    rating = models.DecimalField(max_digits=5, decimal_places=1)
    description = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.pk}"


class PostPhoto(models.Model):
    post = models.ForeignKey(Post, related_name="photos", on_delete=models.CASCADE)
    photo_url = models.URLField(max_length=500)

    def __str__(self):
        return f"Photo for {self.post.id}"
