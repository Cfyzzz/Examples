import json

from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from loguru import logger

from stl_calc import repository

logger.remove()
logger.add(f"logs/stl/control.log", mode="a", format="{time} {level} {message}", level="INFO")


@method_decorator(csrf_exempt, name='dispatch')
def calculation(request):
    """
    Расчет веса и стоимости детали

    :param request:
    json {
        type_plastic: str,
        height_layer: str,
        volume: float
    }
    :return:
    json {
        weight: float,
        cost_detail: float
    }
    """
    if request.method == 'POST':
        post_body = json.loads(request.body)
        type_plastic = post_body.get('type_plastic', '')
        volume = post_body.get('volume', 0.0)
        height_layer = post_body.get('height_layer', '')

        logger.info(f"type_plastic = {type_plastic}; height_layer = {height_layer}; volume = {volume}")

        height_layer_koef = repository.get_height_layer_koef(height_layer)
        plastic = repository.get_plastic(type_plastic)

        weight = round(volume * plastic.density, 3)
        cost_detail = round(volume * plastic.density * plastic.cost_coil * height_layer_koef / 1000, 1)

        data = {
            'weight': weight,
            'cost_detail': cost_detail,
        }
        logger.info(f"data = {data}")
        return JsonResponse(data, status=201)
