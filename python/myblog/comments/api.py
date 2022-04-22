from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt

from comments.services import \
    GetArticleService, \
    PostArticleService, \
    PostCommentToArticleService, \
    PostCommentToCommentService, \
    GetArticleCommentsService, \
    GetCommentCommentsService


@method_decorator(csrf_exempt, name='dispatch')
def article(request):
    """Получение и добавление статьи"""
    if request.method == 'GET':
        service = GetArticleService(request)
        return service.run()
    if request.method == 'POST':
        service = PostArticleService(request)
        return service.run()


def article_comments(request, pk):
    """Получение всех комментариев к статье вплоть до 3 уровня вложенности"""
    if request.method == 'GET':
        service = GetArticleCommentsService(request, pk=pk)
        return service.run()


def comment_comments(request, pk):
    """Получение всех вложенных комментариев для комментария - 3 уровня"""
    if request.method == 'GET':
        service = GetCommentCommentsService(request, pk=pk)
        return service.run()


@method_decorator(csrf_exempt, name='dispatch')
def comment_to_article(request):
    """Добавление комментария к статье"""
    if request.method == 'POST':
        service = PostCommentToArticleService(request)
        return service.run()


@method_decorator(csrf_exempt, name='dispatch')
def comment_to_comment(request):
    """Добавление комментария в ответ на другой комментарий"""
    if request.method == 'POST':
        service = PostCommentToCommentService(request)
        return service.run()
