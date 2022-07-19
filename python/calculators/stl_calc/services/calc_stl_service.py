import os

from django.core.files.storage import default_storage

import stl_calc.services.infrastructure.calculation_volume as calculation_volume

ALLOWED_EXTENSIONS = {'stl'}
UPLOAD_FOLDER = './uploads'
MAX_SIZE = 16 * 1024 * 1024  # Limit 16 M


def calc_stl(file, token) -> float:
    """
    Расчет 3D-модели в stl-файле

    :param file: файл с 3D-моделью
    :param token: уникальный токен заявки
    :return: рассчитанный объём, см куб
    """
    if not os.access(UPLOAD_FOLDER, os.F_OK):
        os.makedirs(UPLOAD_FOLDER)

    if file and file.size < MAX_SIZE and _allowed_file(file.name):
        file_name = token + ".stl"
        path = os.path.join(UPLOAD_FOLDER, file_name)
        with default_storage.open(path, 'wb+') as destination:
            for chunk in file.chunks():
                destination.write(chunk)

        volume = round(calculation_volume.execute(file=path) / 1000, 1)
    else:
        volume = 0.0
    return volume


def _allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS
