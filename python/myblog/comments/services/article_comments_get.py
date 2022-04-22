from django.core.serializers import serialize
from django.http import JsonResponse

from comments.models import Article, Comment
from comments.services.iservice import IService


class GetArticleCommentsService(IService):
    def run(self):
        pk = self.params.get('pk', 0)
        try:
            article = Article.objects.get(id=pk)
        except Article.DoesNotExist:
            return JsonResponse({'message': f"Статья с id={pk} не найдена"}, status=400)

        comments_article_filter = Comment.objects.filter(article=article, level__lte=3)
        if comments_article_filter.count() == 0:
            return JsonResponse(
                {'comments': [], 'message': f"Нет ни одного комментария к этой статье id={pk}"},
                status=400
            )

        comments = comments_article_filter.order_by('parent').all()
        serialized_comments = serialize('python', comments)
        data = {
            'comments': serialized_comments,
            'message': "Ok"
        }
        return JsonResponse(data)
