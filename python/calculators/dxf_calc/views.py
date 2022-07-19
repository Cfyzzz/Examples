from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

import dxf_calc.services as services


@csrf_exempt
def index(request):
    if request.method == 'GET':
        return render(request=request, template_name='dxf_calc/index.html')
    if request.method == 'POST':
        token: str = services.get_new_token()
        file_dxf = request.FILES['file_dxf']
        result = services.calc_dxf(file_dxf, token)
        result['token'] = token
        services.save_data_model(result)
        context = {
            'materials': result['materials'],
            'total_cost': 0.0,
            'token': token,
        }
        return render(request=request, template_name='dxf_calc/result.html', context=context)
    return render(request, template_name='dxf_calc/index.html')
