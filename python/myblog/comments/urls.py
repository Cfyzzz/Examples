from django.urls import path

from . import api

urlpatterns = [
    path('article/', api.article, name='article'),
    path('article/<int:pk>', api.article_comments, name='article_comments'),
    path('<int:pk>', api.comment_comments, name='comment_comments'),
    path('append/article/', api.comment_to_article, name='comment_to_article'),
    path('append/comment/', api.comment_to_comment, name='comment_to_comment'),
]
