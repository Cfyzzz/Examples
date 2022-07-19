from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

from stl_calc import services


@csrf_exempt
def index(request):
    if request.method == 'GET':
        return render(request=request, template_name='stl_calc/index.html')
    if request.method == 'POST':
        token: str = services.get_new_token()
        file_stl = request.FILES['file_stl']
        volume: float = services.calc_stl(file_stl, token)
        types_plastic: list[str] = services.get_types_plastic()
        height_layers: list[str] = services.get_height_layers()
        context = {
            'volume': volume,
            'types_plastic': types_plastic,
            'height_layers': height_layers,
        }
        return render(request=request, template_name='stl_calc/result.html', context=context)
    return render(request, template_name='stl_calc/index.html')
