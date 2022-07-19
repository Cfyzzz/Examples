from django.db import models


class TypePlastic(models.Model):
    name = models.CharField(max_length=20, verbose_name=u"Наименование")
    density = models.FloatField(verbose_name=u"Плотность, гр/см3", default=1.0)
    cost_coil = models.FloatField(verbose_name=u"Стоимость за катушку", default=0.0)
    weight_coil = models.FloatField(verbose_name=u"Вес в катушке, кг", default=0.0)

    class Meta:
        verbose_name = u"тип пластика"
        verbose_name_plural = u"типы пластика"


class HeightLayer(models.Model):
    name = models.CharField(max_length=20, verbose_name=u"Высота слоя")
    koef = models.FloatField(verbose_name=u"Коэффициент", default=0.0)

    class Meta:
        verbose_name = u"высота слоя"
        verbose_name_plural = u"варианты высоты слоя"
