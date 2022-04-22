import json
from sqlite3 import IntegrityError

from django.contrib.auth.models import User
from django.http import JsonResponse

from comments.models import Comment, Article
from comments.services.iservice import IService


class PostCommentToArticleService(IService):
    def run(self):
        post_body = json.loads(self.request.body)
        user_id = post_body.get('user')
        text = post_body.get('text')
        article_id = post_body.get('article')

        try:
            user = User.objects.get(id=user_id)
        except User.DoesNotExist:
            if user_id is None:
                user = None
            else:
                return JsonResponse({'message': f"Пользователь с id={user_id} не найден"}, status=400)

        try:
            article = Article.objects.get(id=article_id)
        except Article.DoesNotExist:
            return JsonResponse({'message': f"Статья с id={article_id} не найдена"}, status=400)

        if not text or text.strip() == "":
            return JsonResponse({'message': f"Пустой комментарий не принимается"}, status=400)

        comment_data = {
            'user': user,
            'text': text,
            'article': article,
            'parent': 0,
        }

        try:
            comment = Comment.objects.create(**comment_data)
            data = {
                'id': comment.id,
                'message': "Ok"
            }
            return JsonResponse(data, status=201)
        except IntegrityError:
            return JsonResponse({'message': "Неправильный запрос"}, status=400)
