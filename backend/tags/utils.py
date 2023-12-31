import requests
import html
from decouple import config


def google_translate_ko_to_en(text):
    url = f"https://translation.googleapis.com/language/translate/v2?key={config('GOOGLE_AUTH_KEY')}"

    data = {
        "q": text,
        "target": "en"
    }
    

    response = requests.post(url, data=data)

    if response.status_code == 200:
        result_json = response.json()
        translations = result_json['data']['translations']
        translated_text = translations[0]['translatedText']
        translated_text = html.unescape(translated_text)
        return translated_text
    else:
        return f"Error: {response.status_code}"


def ml_tagging(review_text, possible_tags):

    API_URL = "https://api-inference.huggingface.co/models/facebook/bart-large-mnli"
    headers = {"Authorization": f'Bearer {config("HUGGINGFACE_AUTH_KEY")}'}

    def query(payload):
        response = requests.post(API_URL, headers=headers, json=payload)
        return response.json()

    output = query({
        "inputs": review_text,
        "parameters": {"candidate_labels": possible_tags},
        "options": {"wait_for_model": True},
    })

    print("output222", output)

    # if output has no labels, return empty dict
    if not output['labels']:
        print ("some error on ml tagging")
        return {}


    label_score_dict = dict(zip(output['labels'], output['scores']))

    return label_score_dict


def ml_sentiment_analysis(review_text):
    API_URL = "https://api-inference.huggingface.co/models/lxyuan/distilbert-base-multilingual-cased-sentiments-student"
    headers = {"Authorization": f"Bearer {config('HUGGINGFACE_AUTH_KEY')}"}

    def query(payload):
        response = requests.post(API_URL, headers=headers, json=payload) # TODO: add retry logic
        return response.json()
        
    output = query({
        "inputs": review_text,
        "options": {"wait_for_model": True},

    })

    print ("sentiment output", output)

    output = output[0]
    output = {i['label']: i['score'] for i in output}

    return output


CATEGORIES = [
    '카페',
    '일식',
    '퓨전',
    '한식',
    '간식',
    '양식',
    '술집',
    '중식',
    '아시아음식',
    '패스트푸드',
    '분식',
    '샐러드',
    '샤브샤브',
    '치킨',
]

FUSION = [
    '뷔페',
    '패밀리레스토랑',
    '퓨전요리',
    '철판요리',
    '푸드코트'
]

KOREAN_FOOD = [
    '도시락', 
    '한식'
]

MEAT = ['육류', '고기', '스테이크', '립', '치킨', '양꼬치']

SEAFOOD = ['해산물', '해물', '생선', '회', '초밥', '롤']

def category_name_to_tags(category_name):
    category_list = category_name.split(' > ')
    
    if len(category_list) < 2:
        return []
    
    tags = []    
    candidate_category = category_list[1]
    if candidate_category in CATEGORIES:
        tags.append(candidate_category)    
    elif candidate_category in FUSION:
        tags.append('퓨전')
    elif candidate_category in KOREAN_FOOD:
        tags.append('한식')
    if any(meat_kind in category_name for meat_kind in MEAT):
        tags.append('고기')
    if any(seafood_kind in category_name for seafood_kind in SEAFOOD):
        tags.append('해산물')
    
    return tags