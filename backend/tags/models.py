from django.db import models

tags = [
    {'ko_label': '한식', 'en_label': 'prefers Korean'},
    {'ko_label': '일식', 'en_label': 'prefers Japanese'},
    {'ko_label': '중식', 'en_label': 'prefers Chinese'},
    {'ko_label': '이탈리안', 'en_label': 'prefers Italian'},
    {'ko_label': '아메리칸', 'en_label': 'prefers American'},
    {'ko_label': '스페인', 'en_label': 'prefers Spanish'},
    {'ko_label': '태국', 'en_label': 'prefers Thai'},
    {'ko_label': '인도', 'en_label': 'prefers Indian'},
    {'ko_label': '베트남', 'en_label': 'prefers Vietnamese'},
    {'ko_label': '스트릿푸드', 'en_label': 'prefers Street Food'},
]


class Tag(models.Model):
    TAG_TYPES = [
        ('from_category', 'from_category'),
        ('user_stat', 'user_stat'),
        ('atmosphere', 'atmosphere'),
    ]

    ko_label = models.CharField(max_length=255)
    en_label = models.CharField(max_length=255)
    type = models.CharField(max_length=20, choices=TAG_TYPES, default='country')

    def __str__(self):
        return self.ko_label

