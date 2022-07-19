from django.contrib import admin

from dxf_calc.models import Sheet, TechnologicalFields


@admin.register(Sheet)
class SheetAdmin(admin.ModelAdmin):
    list_display = ('material', 'thickness', 'width', 'height', 'cost', 'entry', 'path')


@admin.register(TechnologicalFields)
class TechnologicalFieldsAdmin(admin.ModelAdmin):
    list_display = ('horizont_size', 'vertical_size')
