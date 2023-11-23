from unittest import TestCase, skip

from posts.utils import (get_top_tag_after_translation_only_label,
                         get_top_tags_after_translation)
from tags.utils import category_name_to_tags, google_translate_ko_to_en


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
        translated_description = google_translate_ko_to_en(text)        
        possible_tags = [
            'for Family Gathering',
            'trending, hot, instagram',
            'for date, couple',
            'Luxurious, Expensive',
            'Alone',
            'Cost-Effective',
            'excellent staff service',
            'Quiet, Calm',
            'Noisy',
            'Alcohol, Drinking',
        ]
        matching_tag = get_top_tag_after_translation_only_label(
            possible_tags=possible_tags,
            translated_description=translated_description
        )
        
        self.assertEqual(matching_tag, expected_tag)
    

    def run_test_cases(self, test_cases):
        for text, expected_tag in test_cases.items():
            with self.subTest(text=text):
                self.assert_helper(text, expected_tag)
        
    def test_tag_inference(self):
        test_cases = {
            '분위기가 활기찬 이 곳은 친구들과 소주 한 잔하기에 완벽해요. 매콤한 안주가 술맛을 더해줍니다. 즐거운 밤을 위한 최고의 선택이었어요!': 'Alcohol, Drinking',
            '이 식당은 항상 사람들로 북적거려요. 에너지 넘치는 분위기 속에서 맛있는 식사를 할 수 있어 좋았어요. 친구들과 시끌벅적한 저녁을 보내기에 딱이에요.': 'Noisy',
            '이 곳은 조용하고 차분한 분위기가 마음에 들었어요. 혼자서 생각에 잠기며 식사하기에 이상적인 장소예요. 편안하고 고요한 식사를 원한다면 추천합니다.': 'Quiet, Calm',
            '직원들의 친절하고 세심한 서비스가 인상적이었어요. 요청사항에 빠르고 정확하게 반응해주어 더욱 즐거운 식사가 됐습니다. 서비스가 좋은 식당을 찾는다면 여기가 그곳이에요.': 'excellent staff service',
            '맛도 좋고 가격도 합리적이에요. 이 가격에 이런 퀄리티의 음식을 맛볼 수 있다니 놀랍습니다. 학생이나 저렴한 가격을 선호하는 사람에게 추천해요.': 'Cost-Effective',
            '혼자서도 편안하게 식사할 수 있는 곳이에요. 개인적인 공간이 잘 구성되어 있고, 혼자 식사하는 분들도 많아서 부담 없습니다. 혼자 식사하기 좋은 식당을 찾는다면 추천합니다.': 'Alone',
            '이 식당은 고급스러운 분위기와 훌륭한 음식으로 특별한 날을 더욱 특별하게 만들어줘요. 섬세한 맛과 우아한 인테리어가 매력적입니다. 특별한 날을 위한 식당을 찾는다면 여기를 추천해요.': 'Luxurious, Expensive',
            '로맨틱한 분위기와 맛있는 음식으로 데이트하기에 안성맞춤이에요. 아늑한 조명과 멋진 인테리어가 분위기를 더합니다. 데이트하기 좋은 식당을 찾는다면 여기가 완벽해요': 'for date, couple',
            '이 식당은 최근에 핫한 장소로 떠오르고 있어요. 맛있는 음식과 멋진 분위기로 SNS에서도 화제가 되고 있죠. 유행을 좇는 사람들에게 추천합니다.': 'trending, hot, instagram',
            '넓은 자리와 다양한 메뉴로 가족모임하기에 좋아요. 어린이 메뉴부터 어른들을 위한 다양한 요리까지, 모든 가족 구성원이 만족할 수 있습니다. 가족과 함께하는 식사에 추천합니다.': 'for Family Gathering',
            '처음 술 세팅도 다 해주시고 엄청 친절하세요 *..* 기본 안주 두부샐러드도 너무 맛있고 ㅠㅠㅠ 후토마끼 지금까지 다섯번 먹어봤는데 여기가 회 양이 압도적으로 많고 하앙 일단 오이도 없고 최고입니동 ㅜㅜ 바지락술찜은 약간 매콤한게 진짜 후토마끼랑 찰떡이고 너무 술이랑 잘 어울려서 급발진으로 고등어봉초밥 시켰는데 걍 레게노 좐맛이에요 먹어본 데 중에 젤 맛나요 저 여기 그냥 너무 조아요 ~~ 미쳐써요 ~~~~ 간만에 최애 술집 찾았슴동 맨날 올래여 맨날은 구라고 달에 한 번은 꼭 올게여!!!! 사장님 번창하세여 ~~~~~~~~~~': 'Alcohol, Drinking',
            '기대 이상으로 너무 좋았던 곳!! 넓지는 않지만 단 6개의 테이블로 아득하고 조용한 분위기에서 맛있는 식사 할 수 있어 너무 좋았어요!! 친구들도 다 만족한 식사였답니다!': 'Quiet, Calm',
            '영국식 음식을 처음 먹어봤는데 너무 맛있었어요~!! 고기가 너무부드럽고 페스츄리와 가니쉬,소스가 너무 잘어울려서 싹싹 비워먹었어요~!! 샐러드도 버섯이랑 리코타치즈,채소와소스랑 다 잘어울려요ㅎㅎ 다음에 또 서울숲에 갈 일이 있으면 또 방문하고싶어요ㅎㅎ 직원분들도 친절해서 너무 만족한 식당입니다!!!': 'excellent staff service',
            '비교적 저렴한 가격에 비프웰링턴을 먹을 수 있어서 좋았습니다 파스타를 비롯핰 소스에 재료를 아끼지 않는 점이 너무 좋았습니다 다음에 다른 지점 방문할 듯 합니다 항상 지금같이 유지해주세요 감사합니다': 'Cost-Effective',
            '분위기도 좋고 요즘 스러운 중식당 술집이에요. 음악이 조금 크게 들려서 말하기 조금 불편함은 있지만 그래도 분위기를 즐기고 음식도 맛있어요.~!!': 'Noisy',
            '혼자 김밥 한줄 먹기에 부담없이 편한 곳이네요^^': 'Alone',
            '성수동 핫다는 청주한씨 드디어 방문했어요! 모든 메뉴마다 자세하게 설명해주셔서 음미하면서 먹을수 있습니다 전 마지막에 먹었던 누룽지탕이 잊혀지지않네요': 'trending, hot, instagram',
            '오랜만에 여자친구랑 방문했는데 여전히 좋았어요 꼭 한번 방문 해보세요': 'for date, couple',
            '와인바 4층이지만 분위기좋구 테라스도있어서 고급진 분위기였어요 와인마시기 너무좋은곳이네요 ': 'Luxurious, Expensive',
            '네, 예전부터 좋아하던 곳이예요. 부채살 스테이크랑 매쉬드 포테이토랑 같이 먹으면 조화롭기가 세상 맛있답니다😋  가족끼리 가서 먹기도 좋을것 같아요': 'for Family Gathering',
            '물흐르는걸 보며 먹을수있고 발도 담굴수있어서 어린아이있는가족에게 추천하는곳.': 'for Family Gathering',
            '멋진 한옥을 개조한 베이커리 카페. 주말은 사람이 너무 많아 줄서야들어가는 인기좋은 곳. 빵맛도 좋고 음료도 좋고 스텝들도 친절.': 'trending, hot, instagram',
            '안주가 너무 푸짐해서 갈때마다 과음하게 됩니다! ㅎㅎ 사장님이랑 지원분들 모두 친절하세요~': 'Alcohol, Drinking',
            '다들 혼밥을 많이 하는 분위기임 매장 자체가 협소한 편 시오 버터라멘 맛있음 고기는 한 조각이라 아쉬움': 'Alone',
            '분위기가 살짝 어수선한 건 사실이지만 가격 대비 양과 맛을 생각하면 아무 말 없이 먹게 됩니다. 사천오백원 주고 많은 걸 바랄 순 없잖아요.': 'Cost-Effective',
            '김밥 진짜 마싯슴 글구 친절함': 'excellent staff service',
            '여태껏 먹은 중국집 중 단연 1등 부모님 모시고 가기 좋은 집 회래향 탕수육, 짬뽕, 짜장 뭐하나 빠지는 게 없음': 'for Family Gathering'
        }


        self.run_test_cases(test_cases)
