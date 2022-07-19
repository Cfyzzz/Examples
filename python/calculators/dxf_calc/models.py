from django.db import models


class Sheet(models.Model):
    thickness = models.IntegerField(verbose_name=u"Толщина, мм", default=1)
    material = models.CharField(verbose_name=u"Материал", default="Металл", max_length=30)
    width = models.IntegerField(verbose_name=u"Ширина, мм", default=0)
    height = models.IntegerField(verbose_name=u"Высота, мм", default=0)
    cost = models.FloatField(verbose_name=u"Стоимость листа, руб", default=0.0)
    entry = models.FloatField(verbose_name=u"Стоимость врезки, руб", default=0.0)
    path = models.FloatField(verbose_name=u"Стоимость 1м пробега, руб", default=0.0)

    class Meta:
        verbose_name = u"лист"
        verbose_name_plural = u"листы"


class TechnologicalFields(models.Model):
    horizont_size = models.IntegerField(verbose_name=u"Поля слева и справа, мм", default=0)
    vertical_size = models.IntegerField(verbose_name=u"Поля сверху и снизу, мм", default=0)

    def save(self, **kwargs):
        count = TechnologicalFields.objects.all().count()
        if count < 1:
            super(TechnologicalFields, self).save(**kwargs)
        elif self.has_add_permission:
            TechnologicalFields.objects.all().delete()
            super(TechnologicalFields, self).save(**kwargs)

    def has_add_permission(self):
        return not TechnologicalFields.objects.exists()

    class Meta:
        verbose_name = u"технологическое поле"
        verbose_name_plural = u"технологические поля"


class DataModel(models.Model):
    token = models.CharField(verbose_name=u"Токен", default="", max_length=30, unique=True)
    length = models.IntegerField(verbose_name=u"Длина реза, мм", default=0)
    width = models.IntegerField(verbose_name=u"Ширина, мм", default=0)
    height = models.IntegerField(verbose_name=u"Высота, мм", default=0)
    entries = models.IntegerField(verbose_name=u"Количество заходов", default=0)

    class Meta:
        verbose_name = u"чертеж"
        verbose_name_plural = u"чертежи"
