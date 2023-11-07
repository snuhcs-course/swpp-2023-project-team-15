from unittest import TestCase

from tags.utils import category_name_to_tags


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
        
        