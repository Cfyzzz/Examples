from django.contrib import admin
from django.urls import path, include


urlpatterns = [
    path('admin/', admin.site.urls),
    path('dxf/', include('dxf_calc.urls')),
    path('stl/', include('stl_calc.urls')),
]
