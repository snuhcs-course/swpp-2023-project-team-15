# Generated by Django 4.2.6 on 2023-11-05 09:42

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("posts", "0007_restaurant_search_id"),
    ]

    operations = [
        migrations.AddField(
            model_name="restaurant",
            name="category_name",
            field=models.CharField(max_length=255, null=True),
        ),
    ]
