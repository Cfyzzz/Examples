from secrets import token_urlsafe

from django.contrib.auth.base_user import AbstractBaseUser
from django.core import validators
from django.db import models
from django_resized import ResizedImageField

from api.managers import CustomUserManager


def user_directory_path(instance, filename):
    ext = filename.split('.')[-1]
    filename = '{}.{}'.format(token_urlsafe(8), ext)
    return 'user_{0}/{1}'.format(instance.id, filename)


class Organization(models.Model):
    name = models.CharField(max_length=100, blank=False)
    description = models.CharField(max_length=1000, blank=True)

    def __str__(self):
        return self.name


class CustomUser(AbstractBaseUser):
    surname = models.CharField(max_length=50, blank=True)
    name = models.CharField(max_length=50, blank=True)
    phone = models.CharField(max_length=20, blank=True)
    avatar = ResizedImageField('Аватар', size=[200, 200], upload_to=user_directory_path, blank=True)
    organizations = models.ManyToManyField(Organization, blank=True)
    email = models.EmailField(
        validators=[validators.validate_email],
        unique=True,
        blank=False
    )
    is_active = models.BooleanField(default=True)
    is_admin = models.BooleanField(default=False)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []

    objects = CustomUserManager()

    def __str__(self):
        return self.email

    def has_perm(self, perm, obj=None):
        return True

    def has_module_perms(self, app_label):
        return True

    @property
    def is_staff(self):
        return self.is_admin
