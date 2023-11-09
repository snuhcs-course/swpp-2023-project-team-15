from django.contrib.auth import get_user_model
from django.db.models import Avg, Count, StdDev

User= get_user_model()

def get_review_king():
    # get dict of post counts of all users
    user_posts_count_dict = User.objects.annotate(post_count=Count('posts')).values('id', 'post_count').order_by('id').distinct().values()
    user_posts_count_dict = {item['id']: item['post_count'] for item in user_posts_count_dict}

    print("user_posts_count_dict", user_posts_count_dict)

    # sort by post count
    sorted_user_posts_count = sorted(user_posts_count_dict.items(), key=lambda x: x[1], reverse=True)
    # get top 10 percent users with the highest post counts
    top_10_percent = int(len(sorted_user_posts_count) * 0.1)
    top_users = dict(sorted_user_posts_count[:top_10_percent])
    top_users = {k: v for k, v in top_users.items() if v > 0}
    
    # for the users with same post count with top_users[-1], add them to top_users
    for user_id, post_count in sorted_user_posts_count[top_10_percent:]:
        if post_count == sorted_user_posts_count[top_10_percent-1][1]:
            top_users[user_id] = post_count
        else:
            break

    # return top users' ids
    return list(top_users.keys())

def get_influencer():
    # get dict of follower counts of all users
    user_followers_count_dict = User.objects.annotate(follower_count=Count('followees')).values('id', 'follower_count').order_by('id').distinct().values()
    user_followers_count_dict = {item['id']: item['follower_count'] for item in user_followers_count_dict}

    print("user_followers_count_dict", user_followers_count_dict)

    # sort by follower count
    sorted_user_followers_count = sorted(user_followers_count_dict.items(), key=lambda x: x[1], reverse=True)
    # get top 10 percent users with the highest follower counts
    top_10_percent = int(len(sorted_user_followers_count) * 0.1)
    top_users = dict(sorted_user_followers_count[:top_10_percent])
    top_users = {k: v for k, v in top_users.items() if v > 0}

    # for the users with same follower count with top_users[-1], add them to top_users
    for user_id, follower_count in sorted_user_followers_count[top_10_percent:]:
        if follower_count == sorted_user_followers_count[top_10_percent-1][1]:
            top_users[user_id] = follower_count
        else:
            break

    print("top_users", top_users)

    # return top users' ids
    return list(top_users.keys())

def get_review_angel():
    # get dict of average rating of all users
    user_avg_rating_dict = User.objects.annotate(avg_rating=Avg('posts__rating')).values('id', 'avg_rating').order_by('id').distinct().values()

    # if user has no rating, exclude them
    user_avg_rating_dict = {item['id']: item['avg_rating'] for item in user_avg_rating_dict if item['avg_rating'] is not None}

    print("user_avg_rating_dict", user_avg_rating_dict)

    # sort by average rating
    sorted_user_avg_rating = sorted(user_avg_rating_dict.items(), key=lambda x: x[1], reverse=True)
    print("sorted_user_avg_rating", sorted_user_avg_rating)
    # get top 10 percent users with the highest average rating
    top_10_percent = max(1, int(len(user_avg_rating_dict) * 0.1))

    top_users = dict(sorted_user_avg_rating[:top_10_percent])
    top_users = {k: v for k, v in top_users.items() if v > 0}


    # for the users with same average rating with top_users[-1], add them to top_users
    for user_id, avg_rating in sorted_user_avg_rating[top_10_percent:]:
        if avg_rating == sorted_user_avg_rating[top_10_percent-1][1]:
            top_users[user_id] = avg_rating
        else:
            break

    print("top_users", top_users)

    # return top users' ids
    return list(top_users.keys())


def get_review_gourmet():
    # get dict of average standard deviation of all users' ratings
    user_stddev_dict = User.objects.annotate(std_dev=StdDev('posts__rating')).values(
        'id', 'std_dev').order_by('id').distinct().values()

    # if user has no standard deviation or not enough ratings to compute one, exclude them
    user_stddev_dict = {item['id']: item['std_dev']
                        for item in user_stddev_dict if item['std_dev'] is not None}

    # sort by standard deviation, highest first
    sorted_user_stddev = sorted(
        user_stddev_dict.items(), key=lambda x: x[1], reverse=True)
    print("sorted_user_stddev", sorted_user_stddev)
    # get top 10 percent users with the highest standard deviation
    top_10_percent = max(1, int(len(user_stddev_dict) * 0.1))

    # select these top users, ensuring they have a standard deviation greater than zero
    top_users = dict(sorted_user_stddev[:top_10_percent])
    top_users = {k: v for k, v in top_users.items() if v > 0}
    
    print("top_users", top_users)

    # for users with the same standard deviation as the last user in the top 10 percent, include them
    for user_id, std_dev in sorted_user_stddev[top_10_percent:]:
        if std_dev == sorted_user_stddev[top_10_percent-1][1]:
            top_users[user_id] = std_dev
        else:
            break

    # return top users' ids
    return list(top_users.keys())
