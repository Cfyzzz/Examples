from dataclasses import dataclass

from django.core.exceptions import ObjectDoesNotExist
from loguru import logger

from stl_calc.models import HeightLayer, TypePlastic

logger.remove()
logger.add(f"logs/stl/repository.log", mode="a", format="{time} {level} {message}", level="INFO")


@dataclass
class Plastic:
    density: float
    cost_coil: float
    weight_coil: float


def get_height_layer_koef(height_layer: str) -> float:
    """
    Возвращает коэффициент соответствующий, параметру height_layer

    :param height_layer: высота слоя
    :return: коэффициент пересчета сложности изделия
    """
    try:
        logger.info(f"In: height_layer = '{height_layer[:50]}'")
        result = HeightLayer.objects.get(name=height_layer)
    except ObjectDoesNotExist:
        logger.error(f"HeightLayer with layer {height_layer} doesn't exist.")
        return 0.0
    return result.koef


def get_plastic(type_plastic: str) -> Plastic:
    """
    Возвращает информацию по материалу

    :param type_plastic: тип материала
    :return: объект класса Plastic с полной информацией по материалу
    """
    try:
        logger.info(f"IN: type_plastic = '{type_plastic[:20]}'")
        type_plastic: TypePlastic = TypePlastic.objects.get(name=type_plastic)
    except ObjectDoesNotExist:
        logger.error(f"TypePlastic with type {type_plastic} doesn't exist.")
        return Plastic(
            density=0.0,
            cost_coil=0.0,
            weight_coil=0.0
        )
    return Plastic(
        density=type_plastic.density,
        cost_coil=type_plastic.cost_coil,
        weight_coil=type_plastic.weight_coil
    )


def get_all_types_plastic() -> list[str]:
    """
    Список возможных типов пластика

    :return: список наименований пластика
    """
    result = []
    for type_plastic in TypePlastic.objects.all():
        result.append(type_plastic.name)
    logger.info(f"Get all types plastic len={len(result)}")
    return result


def get_all_height_layers() -> list[str]:
    """
    Список возможных уровней высот слоя

    :return: список возможных уровней высот слоя
    """
    result = []
    for height_layer in HeightLayer.objects.all():
        result.append(height_layer.name)
    logger.info(f"Get all height layers len={len(result)}")
    return result
