from django.core.serializers import serialize
from django.http import JsonResponse

from comments.models import Article
from comments.services.iservice import IService


class GetArticleService(IService):
    def run(self):
        articles_count = Article.objects.count()
        if articles_count == 0:
            return JsonResponse({'articles': [], 'message': "Нет ни одной статьи"}, status=400)

        articles = Article.objects.all()
        serialized_articles = serialize('python', articles)
        data = {
            'articles': serialized_articles,
            'message': "Ok"
        }
        return JsonResponse(data)
