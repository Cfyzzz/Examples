from django.urls import re_path, path, include

from . import views

urlpatterns = [
    path('/', include('django.contrib.auth.urls')),

    re_path(r'^$', views.dashboard, name='dashboard'),
    re_path(r'^clients/create/$', views.register, name='register'),
    re_path(r'^clients/(?P<friend_id>\d+)/match/$', views.match, name='match'),
    re_path(r'^clients/edit/$', views.edit, name='edit'),
    re_path(r'^list/$', views.list_members, name="list_members"),
    re_path(r'^list/(?P<distance>\d+)/$', views.list_members_into_distance, name="list_members_into_distance"),
]
