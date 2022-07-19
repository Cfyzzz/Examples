from django.urls import path

from . import views, api


urlpatterns = [
    path('', views.index, name='index'),
    path('api/calculation', api.calculation, name='calculation_stl')
]
