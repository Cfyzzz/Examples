from dataclasses import dataclass


@dataclass
class Point:
    x: float = 0.0
    y: float = 0.0


class Segment:
    def __init__(self):
        self.points = [Point() for _ in range(4)]

    def calc_p1(self, t, b):
        # TODO - Здесь реализация
        ...

    def calc(self, t, b):
        # TODO - Здесь реализация
        ...


def copy(p: Point) -> Point:
    return Point(p.x, p.y)


def loopResolve(verts, step, dist=None):
    # TODO - Здесь реализация
    ...


def length_line(a, b):
    """Длина линии между двумя точками"""
    return ((a.x - b.x) ** 2 + (a.y - b.y) ** 2) ** 0.5
