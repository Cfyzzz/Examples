from django.contrib import admin
from django.contrib.auth import get_user_model
from django.contrib.auth.admin import UserAdmin

from .forms import CustomUserCreationForm, CustomUserChangeForm
from .models import CustomUser


class CustomUserAdmin(UserAdmin):
    add_form = CustomUserCreationForm
    form = CustomUserChangeForm
    model = CustomUser
    list_display = ['avatar', 'username', 'surname', 'email', 'phone']


class OrganizationAdmin(admin.ModelAdmin):
    list_display = ['name', 'description']


admin.site.register(CustomUser, CustomUserAdmin)
