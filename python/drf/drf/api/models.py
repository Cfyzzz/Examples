from django.core import validators
from django.db import models
from django.contrib.auth.models import AbstractUser
from django.contrib.auth.base_user import AbstractBaseUser

from api.managers import CustomUserManager


class Organization(models.Model):
    name = models.CharField(max_length=100, blank=False)
    description = models.CharField(max_length=1000, blank=True)

    def __str__(self):
        return self.name


class CustomUser(AbstractUser):
    surname = models.CharField(max_length=50, blank=True)
    phone = models.CharField(max_length=20, blank=True)
    avatar = models.ImageField('Аватар', upload_to='avatars/%Y/%m/%d', blank=True)
    organizations = models.ManyToManyField(Organization, blank=True)
    email = models.EmailField(
        validators=[validators.validate_email],
        unique=True,
        blank=False
    )
    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ('username',)

    objects = CustomUserManager()

    def __str__(self):
        return self.username
