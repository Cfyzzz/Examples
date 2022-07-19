from django.contrib import admin

from .models import HeightLayer, TypePlastic


@admin.register(HeightLayer)
class HeightLayerAdmin(admin.ModelAdmin):
    list_display = ('name', 'koef')


@admin.register(TypePlastic)
class TypePlasticAdmin(admin.ModelAdmin):
    list_display = ('name', 'density', 'cost_coil', 'weight_coil')
