from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from users.models import User, Follow


# Register your models here.
@admin.register(User)
class CustomUserAdmin(UserAdmin):
    fieldsets = (
        (
            "Profile",
            {
                "fields": (
                    "avatar_url",
                    "username",
                    "password",
                    "email",
                    "description",
                    "tags",
                ),
            },
        ),
        # (
        #     "Permissions",
        #     {
        #         "fields": (
        #             "is_superuser",
        #             "groups",
        #             "user_permissions",
        #         ),
        #         "classes": ("collapse",),
        #     },
        # ),
        # (
        #     "Important Dates",
        #     {
        #         "fields": ("last_login", "date_joined"),
        #         "classes": ("collapse",),
        #     },
        # ),
    )

    list_display = (
        "username",
        "description",
    )

@admin.register(Follow)
class FollowAdmin(admin.ModelAdmin):
    list_display = (
        "follower",
        "followee",
        "created",
    )
    list_filter = (
        "follower",
        "followee",
    )
    search_fields = (
        "follower__username",
        "followee__username",
    )