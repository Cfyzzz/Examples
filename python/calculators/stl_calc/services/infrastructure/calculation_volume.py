import os

from loguru import logger


def execute(file: str) -> float:
    """
    Расчет объема 3D-модели

    :param file: путь к файлу
    :return: объём, мм куб.
    """
    name = os.path.basename(file)
    logger.remove()
    logger.add(f"logs/file_{name.split('.')[0]}.log", mode="w", format="{time} {level} {message}", level="DEBUG")

    volume = 0.0
    # TODO - Здесь реализация
    ...
    return volume
