from django.contrib.auth.models import User
from django.db import models


class Profile(models.Model):
    GENDER_CHOICE = (
        ("M", "М"),
        ("F", "Ж"),
        (None, "-")
    )

    user = models.OneToOneField(User, on_delete=models.CASCADE)
    sex = models.CharField('Пол', max_length=1,
                           choices=GENDER_CHOICE,
                           blank=True)
    photo = models.ImageField('Аватар', upload_to='avatars/%Y/%m/%d', blank=True)
    longitude = models.FloatField('Долгота', default=0.0)
    latitude = models.FloatField('Широта', default=0.0)

    def __str__(self):
        return 'Profile for user {}'.format(self.user.username)

    class Meta:
        verbose_name = 'Профиль'
        verbose_name_plural = 'Профили'


class Friend(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    users_friend = models.ForeignKey(User, related_name='users_friend', on_delete=models.CASCADE)
    confirmed = models.BooleanField('Подтвержденный', default=False)

    def __str__(self):
        return str(self.user)

    class Meta:
        verbose_name = 'Друг'
        verbose_name_plural = 'Друзья'
