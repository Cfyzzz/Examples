from django.contrib.auth.models import User
from django.db import models


class Article(models.Model):
    author = models.CharField(max_length=100, blank=True)


class Comment(models.Model):
    parent = models.IntegerField(default=0)
    article = models.ForeignKey(Article, on_delete=models.CASCADE)
    text = models.TextField()
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=True)
    level = models.IntegerField(default=1)
