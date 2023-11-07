from unittest import TestCase

from posts.views import (get_top_tag_after_translation_only_label,
                         get_top_tags_after_translation)
from tags.utils import category_name_to_tags, deepl_translate_ko_to_en


# Create your tests here.
class TagGenerateTestCase(TestCase):
    def assert_helper(self, category_name, expected_tags):
        result = category_name_to_tags(category_name)
        self.assertEqual(result, expected_tags)
        
    def test_simple_tag(self):
        self.assert_helper("음식점 > 카페", ["카페"])
        
        
    
    def test_missing_tag(self):        
        self.assert_helper("음식점 > 아무거나실제로없는거 > 또없는거", [])
    
    def test_short_tag(self):        
        self.assert_helper("음식점", [])
    
    def test_merge(self):
        self.assert_helper("음식점 > 뷔페", ["퓨전"])
        
    def test_fusion(self):
        self.assert_helper("음식점 > 퓨전요리", ["퓨전"])
        
    def test_korean_food(self):
        self.assert_helper("음식점 > 도시락 > 한솥도시락", ["한식"])
        
    def test_meat(self):
        self.assert_helper("음식점 > 한식 > 육류,고기 > 삼겹살", ["한식", "고기"])
        self.assert_helper("음식점 > 양식 > 스테이크,립", ["양식", "고기"])
        
    def test_seafood(self):
        self.assert_helper("음식점 > 한식 > 해물,생선 > 회", ["한식", "해산물"])
        self.assert_helper("음식점 > 일식 > 초밥,롤", ["일식", "해산물"])
        self.assert_helper("음식점 > 일식 > 참치회", ["일식", "해산물"])
        self.assert_helper("음식점 > 양식 > 해산물 > 바닷가재", ["양식", "해산물"])


class TagInferenceTestCase(TestCase):
    def assert_helper(self, text, expected_tag):
        translated_description = deepl_translate_ko_to_en(text)        
        possible_tags = [
            'preferes for Family Gathering',
            'prefers for Group Dining',
            'prefers for a Date',
            'prefers Upscale',
            'prefers Expensive',
            'prefers Cost-Effective',
            'prefers Instagrammable',
            'prefers Quiet',
            'prefers Lively',
            'prefers With Alcohol',
        ]
        matching_tag = get_top_tag_after_translation_only_label(
            possible_tags=possible_tags,
            translated_description=translated_description
        )
        
        self.assertEqual(matching_tag, expected_tag)
    
    def test_nomatch(self):
        self.assert_helper('너무 맛없고 비려요!', None)
        
    def test_quite(self):
        self.assert_helper('고등어구이가 별미입니다. 분위기도 조용하고, 사시미도 맛있어요.', 'prefers Quiet')
    
    def test_cost_effective(self):
        self.assert_helper('개인적으로 이 메뉴 젤 만족! 5000원이에요~', 'prefers Cost-Effective')
    
    def test_expensive(self):
        self.assert_helper('가격이 싸진 않지만 맛있어요.', 'prefers Expensive')
        
