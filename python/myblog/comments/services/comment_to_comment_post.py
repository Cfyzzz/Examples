import json
from sqlite3 import IntegrityError

from django.contrib.auth.models import User
from django.http import JsonResponse

from comments.models import Comment
from comments.services.iservice import IService


class PostCommentToCommentService(IService):
    def run(self):
        post_body = json.loads(self.request.body)
        user_id = post_body.get('user')
        text = post_body.get('text')
        parent_id = post_body.get('parent')

        try:
            user = User.objects.get(id=user_id)
        except User.DoesNotExist:
            if user_id is None:
                user = None
            else:
                return JsonResponse({'message': f"Пользователь с id={user_id} не найден"}, status=400)

        try:
            parent_comment = Comment.objects.get(id=parent_id)
            article = parent_comment.article
        except Comment.DoesNotExist:
            return JsonResponse({'message': f"Комментируемый пост с id={parent_id} не найден"}, status=400)

        if not text or text.strip() == "":
            return JsonResponse({'message': f"Пустой комментарий не принимается"}, status=400)

        comment_data = {
            'user': user,
            'text': text,
            'article': article,
            'parent': parent_id,
            'level': parent_comment.level+1,
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
