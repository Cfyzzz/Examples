from django.contrib import admin
from .models import Profile, Friend


class ProfileAdmin(admin.ModelAdmin):
    list_display = ['user', 'sex', 'photo', 'longitude', 'latitude']
    list_filter = ['sex']


class FriendAdmin(admin.ModelAdmin):
    list_display = ['user', 'users_friend', 'confirmed']
    list_filter = ['users_friend', 'confirmed']


admin.site.register(Profile, ProfileAdmin)
admin.site.register(Friend, FriendAdmin)
