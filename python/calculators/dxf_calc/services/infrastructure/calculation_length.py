import os
from math import dist, pi, cos, sin
from typing import Optional

import ezdxf
from PIL import Image
from PIL import ImageDraw
from loguru import logger

from dxf_calc.services.infrastructure.splines import loopResolve, Point

DELTA_Y = 100
DELTA_X = 600


class Rect:
    def __init__(self):
        self.x_min = float('+inf')
        self.x_max = float('-inf')
        self.y_min = float('+inf')
        self.y_max = float('-inf')

    def get_point(self, point: Point):
        if point.x < self.x_min:
            self.x_min = point.x
        if point.x > self.x_max:
            self.x_max = point.x
        if point.y < self.y_min:
            self.y_min = point.y
        if point.y > self.y_max:
            self.y_max = point.y

    @property
    def width(self):
        return self.x_max - self.x_min

    @property
    def height(self):
        return self.y_max - self.y_min


def execute(file_name, threshold=0.1, visual_debug=True, is_show=False) -> Optional[tuple[int, int, Rect]]:
    """
    Выполнить расчет длины реза

    :param file_name: иимя файла
    :param threshold: близкое расстояние считать единым резом, по умолчанию 0.1
    :param visual_debug: показывать дополнительную информацию на картинке, по умолчанию True (только в случае is_show)
    :param is_show: показывать картинку с моделью, по умолчанию False
    :return: длина реза, мм; количество заходов; Rect описание габаритов. В случае невозможности расчетов
    вернется None
    """
    name = os.path.basename(file_name)
    logger.remove()
    logger.add(f"logs/file_{name.split('.')[0]}.log", mode="w", format="{time} {level} {message}", level="DEBUG")

    try:
        doc = ezdxf.readfile(file_name)
    except IOError:
        logger.error(f"Not a DXF file or a generic I/O error.")
        return
    except ezdxf.DXFStructureError:
        logger.error(f"Invalid or corrupted DXF file.")
        return

    # iterate over all entities in modelspace
    msp = doc.modelspace()
    logger.info(f"Count parts: {len(msp)}")
    logger.info(f"\tcount line: {len(msp.query('LINE'))}")
    logger.info(f"\tcount arcs: {len(msp.query('ARC'))}")
    logger.info(f"\tcount circles: {len(msp.query('CIRCLE'))}")
    logger.info(f"\tcount xlines: {len(msp.query('XLINE'))}")
    logger.info(f"\tcount splines: {len(msp.query('SPLINE'))}")
    logger.info(f"\tcount lwpolyline: {len(msp.query('LWPOLYLINE'))}")

    rect = Rect()
    totaldist = 0
    # TODO - Здесь реализация
    ...
    return totaldist, len(parts2), rect
