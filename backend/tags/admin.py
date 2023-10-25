from django.contrib import admin

from tags.models import Tag


# Register your models here.
@admin.register(Tag)
class TagAdmin(admin.ModelAdmin):
    list_display = ['id', 'type', 'ko_label', 'en_label']
    search_fields = ['ko_label', 'en_label']
    list_filter = ['ko_label', 'en_label']