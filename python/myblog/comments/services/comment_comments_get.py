from django.core.serializers import serialize
from django.http import JsonResponse

from comments.models import Article, Comment
from comments.services.iservice import IService


class GetCommentCommentsService(IService):
    def run(self):
        pk = self.params.get('pk', 0)
        try:
            comment = Comment.objects.get(id=pk)
        except Comment.DoesNotExist:
            return JsonResponse({'message': f"Комментарий с id={pk} не найден"}, status=400)

        base_level = comment.level
        comments_comment_filter = Comment.objects.filter(
            article=comment.article,
            level__lte=base_level + 3,
            level__gt=base_level
        )
        if comments_comment_filter.count() == 0:
            return JsonResponse(
                {'comments': [], 'message': f"Нет ни одного комментария к этому комментарию id={pk}"},
                status=400
            )

        comments = comments_comment_filter.order_by('parent').all()
        valid_parent_pk = [pk]
        result_comments = []
        for comment_next in comments:
            if comment_next.parent in valid_parent_pk:
                result_comments.append(comment_next)
                valid_parent_pk.append(comment_next.pk)

        serialized_comments = serialize('python', result_comments)
        data = {
            'comments': serialized_comments,
            'message': "Ok"
        }
        return JsonResponse(data)
