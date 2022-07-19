import json

from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from loguru import logger

from dxf_calc import repository
from dxf_calc.repository import MaterialSheet

logger.remove()
logger.add(f"logs/dxf/control.log", mode="a", format="{time} {level} {message}", level="INFO")


@method_decorator(csrf_exempt, name='dispatch')
def calculation(request):
    if request.method == 'GET':
        get_body = request.GET
        material = get_body.get('material', "")
        token = get_body.get('token', "")
        thickness = get_body.get('thickness', 0)

        logger.info(f"material = {material}; token = {token}")

        data_model = repository.get_data_model(token=token)
        material_sheets = repository.get_sheet_by_material_and_thickness(material=material, thickness=thickness)
        fields = repository.get_fields()

        total_cost = 0
        if data_model and material_sheets and fields:
            data_model['width'] += fields['horizontal']
            data_model['height'] += fields['vertical']
            sheet = material_sheets[0]
            total_cost = round(
                data_model['width'] * data_model['height'] / (sheet.width * sheet.height) * sheet.cost
                + sheet.path * data_model['length'] / 1000 + sheet.entry * data_model['entries']
            )
        else:
            logger.info("Невозможен расчет стоимости детали")
            if not data_model:
                logger.info(f"Данные по модели ошибочны")
            if not material_sheets:
                logger.info(f"Данные по листу не найдены")
            if not fields:
                logger.info(f"Данные по технологическим полям не заданы")

        logger.info(f"total_cost = {total_cost}")
        return JsonResponse({'total_cost': total_cost}, status=200)
