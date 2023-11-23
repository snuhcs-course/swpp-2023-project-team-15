import threading
from tags.models import Tag
from tags.utils import (category_name_to_tags, google_translate_ko_to_en,
                        ml_sentiment_analysis, ml_tagging)

def get_top_tag_after_translation_only_label(possible_tags, translated_description):
    label_score_dict = ml_tagging(translated_description, possible_tags)
    max_label = max(label_score_dict, key=label_score_dict.get)
    
    if label_score_dict[max_label] > 0.23:
        return max_label
    return None

def get_top_tags_after_translation(possible_tags, translated_description):
    max_label = get_top_tag_after_translation_only_label(possible_tags, translated_description)

    if max_label is not None:
        matching_tag = Tag.objects.filter(en_label=max_label).first()
        return matching_tag
    return None

def create_tags_on_thread(post):
    print("Thread started for post ", post.id)
    # calculate category tags
    category_name = post.restaurant.category_name
    if category_name is not None:
        category_tags = category_name_to_tags(category_name)
        for tag in category_tags:
            tag_obj, _ = Tag.objects.get_or_create(type='from_category', ko_label=tag, en_label='')
            post.tags.add(tag_obj)
    
    #translate
    if len(post.description) < 5:
        print(f'create tag and sentiment skipped: description too short: {post.description}.')
        return
    translated_description = google_translate_ko_to_en(post.description)
    print('translated description', translated_description)

    # calculate atmosphere tags
    tags_atmosphere = Tag.objects.filter(type='atmosphere').values('en_label')
    possible_tags = [tag['en_label'] for tag in tags_atmosphere]
    matching_tag = get_top_tags_after_translation(possible_tags, translated_description)
    print('fount tag', matching_tag)
    if matching_tag is not None:
        post.tags.add(matching_tag)

    # calculate sentiment for post
    sentiment_dict = ml_sentiment_analysis(translated_description)
    positive_score = sentiment_dict['positive']
    neutral_score = sentiment_dict['neutral']
    negative_score = sentiment_dict['negative']

    print ('positive_score', positive_score)
    print ('neutral_score', neutral_score)
    print ('negative_score', negative_score)
    # get sentiment
    post.sentiment = round(positive_score - negative_score, 4)
    print ('sentiment', post.sentiment)
    post.save()

    print("Thread finished")