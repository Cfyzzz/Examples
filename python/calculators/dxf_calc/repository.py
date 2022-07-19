from dataclasses import dataclass
from dataclasses import dataclass
from typing import Any

from django.core.exceptions import ObjectDoesNotExist
from loguru import logger

from dxf_calc.models import Sheet, DataModel, TechnologicalFields

logger.remove()
logger.add(f"logs/dxf/repository.log", mode="a", format="{time} {level} {message}", level="INFO")


@dataclass
class MaterialSheet:
    material: str
    thickness: int
    width: int
    height: int
    cost: float
    entry: int
    path: int


def get_all_sheets() -> list[MaterialSheet]:
    """
    Возвращает список возможных листов из разных материалов, разной толщины

    :return: Список листов
    """
    result = []
    for sheet in Sheet.objects.all():
        material_sheet = MaterialSheet(
            material=sheet.material,
            thickness=sheet.thickness,
            width=sheet.width,
            height=sheet.height,
            cost=sheet.cost,
            entry=sheet.entry,
            path=sheet.path,
        )
        result.append(material_sheet)
    logger.info(f"Get all sheets len={len(result)}")
    return result


def get_all_materials() -> list[tuple[str, int]]:
    """
    Возвращает список всех вариантов по материалам

    :return: список кортежей (материал, толщина листа)
    """
    result = []
    for sheet in Sheet.objects.all():
        result.append((sheet.material, sheet.thickness))
    result = list(set(result))
    logger.info(f"Get all material sheet len={len(result)}")
    return result


def save_data_model(data: dict[str, Any]):
    """
    Сохраняет данные в таблицу DataModel

    :param data: данные для сохранения (соответствует полям класса DataModel)
    :return: None
    """
    logger.info(f"Save data model: {data}")
    params = {}
    available_fields = [field.name for field in DataModel._meta.get_fields()]
    for key, value in data.items():
        if key in available_fields:
            params[key] = value
    data_model = DataModel(**params)
    data_model.save()


def get_data_model(token: str) -> dict[str, Any]:
    """
    Получает данные из DataModel по токену и возвращает информацию в виде словаря

    :param token: токен
    :return: словарь с данными или None в случае отсутствия такого токена
    """
    try:
        data_model = DataModel.objects.get(token=token)
    except ObjectDoesNotExist:
        logger.error(f"The token '{token}' not in DataModel")
        return None
    return {
        "token": data_model.token,
        "length": data_model.length,
        "width": data_model.width,
        "height": data_model.height,
        "entries": data_model.entries
    }


def get_sheet_by_material_and_thickness(material: str, thickness: int) -> list[MaterialSheet]:
    """
    Получить параметры для расчетов по заданному материалу и толщине

    :param material: материал
    :param thickness: толщина материала (мм)
    :return: список всех возможных листов согласно материалу
    """
    result = []
    sheets = Sheet.objects.filter(material=material, thickness=thickness)
    for sheet in sheets:
        result.append(
            MaterialSheet(
                material=sheet.material,
                thickness=sheet.thickness,
                width=sheet.width,
                height=sheet.height,
                cost=sheet.cost,
                entry=sheet.entry,
                path=sheet.path,
            )
        )
    return result


def get_fields() -> dict[str, int]:
    """
    Возвращает технологические поля или None в случае их отсутствия

    :return: {'horizontal': int, 'vertical': int}
    """
    tech_fields = TechnologicalFields.objects.all()
    for fields in tech_fields:
        return {'horizontal': fields.horizont_size, 'vertical': fields.vertical_size}
    return None
