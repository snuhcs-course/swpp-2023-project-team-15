# Generated by Django 4.2.6 on 2023-11-06 12:46

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("tags", "0003_tag_type"),
    ]

    operations = [
        migrations.AlterField(
            model_name="tag",
            name="type",
            field=models.CharField(
                choices=[
                    ("from_category", "from_category"),
                    ("atmosphere", "atmosphere"),
                ],
                default="country",
                max_length=20,
            ),
        ),
    ]