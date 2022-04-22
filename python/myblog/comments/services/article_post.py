import json
from sqlite3 import IntegrityError

from django.http import JsonResponse

from comments.models import Article
from comments.services.iservice import IService


class PostArticleService(IService):
    def run(self):
        post_body = json.loads(self.request.body)

        author = post_body.get('author')
        article_data = {
            'author': author,
        }
        try:
            article = Article.objects.create(**article_data)
            data = {
                'id': article.id,
                'author': article.author,
                'message': "Ok"
            }
            return JsonResponse(data, status=201)
        except IntegrityError:
            return JsonResponse({'message': "Неправильный запрос"}, status=400)
