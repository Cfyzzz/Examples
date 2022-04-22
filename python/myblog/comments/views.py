import requests
from django.http import HttpRequest
from django.shortcuts import render, redirect


class Comment:
    def __init__(self):
        self.pk = 0
        self.text = 0
        self.level = 0
        self.children = []


def home(request):
    headers = {'Content-Type': 'application/json'}
    url = request.build_absolute_uri("/api/comment/article/")
    resp = requests.get(url, headers=headers)
    if resp.status_code != 200:
        return redirect(request.url)
    body = resp.json()
    articles = body['articles']
    text = ""
    for article in articles:
        comments_article = {}
        text += f"Article {article['fields']['author']}'s:\n"
        processing_top_comments(article_pk=article['pk'], url=url, comments_article=comments_article)
        for comment in comments_article.values():
            text += get_text_all_children(comment)

    return render(request, 'home.html', {'text': text})


def processing_top_comments(article_pk, url, comments_article):
    headers = {'Content-Type': 'application/json'}
    url_pk = url + str(article_pk)
    resp = requests.get(url_pk, headers=headers)
    if resp.status_code != 200:
        return redirect(url)
    body = resp.json()
    comments = body['comments']
    for comment in comments:
        top_comment = Comment()
        top_comment.pk = comment['pk']
        top_comment.text = comment['fields']['text']
        top_comment.level = comment['fields']['level']
        parent = comment['fields']['parent']
        if parent in comments_article:
            parent_comment = comments_article.get(parent)
            parent_comment.children.append(top_comment)
        else:
            comments_article[top_comment.pk] = top_comment

        if top_comment.level == 3:
            processing_rest_comments(pk=top_comment.pk, url=url.replace('/article/', "/"),
                                     comments_article=comments_article, level=3)


def processing_rest_comments(pk, url, comments_article, level):
    headers = {'Content-Type': 'application/json'}
    url_pk = url + str(pk)
    resp = requests.get(url_pk, headers=headers)
    if resp.status_code != 200:
        return redirect(url)
    body = resp.json()
    comments = body['comments']
    for comment in comments:
        rest_comment = Comment()
        rest_comment.pk = comment['pk']
        rest_comment.text = comment['fields']['text']
        rest_comment.level = comment['fields']['level']
        parent = comment['fields']['parent']
        if parent in comments_article:
            parent_comment = comments_article.get(parent)
            parent_comment.children.append(rest_comment)
        else:
            comments_article[rest_comment.pk] = rest_comment

        if rest_comment.level == level + 3:
            processing_rest_comments(pk=rest_comment.pk, url=url.replace('/article/', "/"),
                                     comments_article=comments_article, level=rest_comment.level)


def get_text_all_children(comment: Comment):
    text = f"{'..'*comment.level}{comment.text} pk={comment.pk}\n"
    for child_comment in comment.children:
        text += get_text_all_children(child_comment)
    return text
