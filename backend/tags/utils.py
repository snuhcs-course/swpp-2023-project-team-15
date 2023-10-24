import requests
from decouple import config


def deepl_translate_ko_to_en(text):
    url = "https://api-free.deepl.com/v2/translate"

    data = {
        'text': text,
        'source_lang': 'KO',
        'target_lang': 'EN',
    }

    headers = {
        'Authorization': f'DeepL-Auth-Key {config("DEEPL_AUTH_KEY")}',
    }

    response = requests.post(url, headers=headers, data=data)

    if response.status_code == 200:
        result_json = response.json()
        translations = result_json['translations']
        translated_text = translations[0]['text']
        return translated_text
    else:
        return f"Error: {response.status_code}"
