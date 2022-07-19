import os
from typing import Any

from django.core.files.storage import default_storage

import dxf_calc.services.infrastructure.calculation_length as calculation_length
from dxf_calc.services.infrastructure import get_all_materials

ALLOWED_EXTENSIONS = {'dxf'}
UPLOAD_FOLDER = './uploads'
MAX_SIZE = 16 * 1024 * 1024  # Limit 16 M


def calc_dxf(file, token) -> dict[str, Any]:
    """
    Расчет модели из dxf-файла

    :param file: файл модели в формате dxf
    :param token: уникальный токен заявки
    :return: length - длина реза, мм; width - ширина, мм; height - высота, мм; entries - количество заходов;
        список материалов по ключу materials
    """
    if not os.access(UPLOAD_FOLDER, os.F_OK):
        os.makedirs(UPLOAD_FOLDER)

    data = {
        'length': 0.0,
        'width': 0.0,
        'height': 0.0,
        'entries': 0,
        'materials': [],
    }
    # TODO - Здесь реализация
    ...
    return data


def _allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS
