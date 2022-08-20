from secrets import token_urlsafe

from django.contrib.auth.base_user import AbstractBaseUser
from django.core import validators
from django.db import models

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
    avatar = models.ImageField('Аватар', upload_to=user_directory_path, blank=True)
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
        "Does the user have a specific permission?"
        # Simplest possible answer: Yes, always
        return True

    def has_module_perms(self, app_label):
        "Does the user have permissions to view the app `app_label`?"
        # Simplest possible answer: Yes, always
        return True

    @property
    def is_staff(self):
        "Is the user a member of staff?"
        # Simplest possible answer: All admins are staff
        return self.is_admin
