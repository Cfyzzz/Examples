import os
from io import BytesIO
from math import radians, asin, sqrt, sin, cos
from pathlib import Path

from PIL import Image, ImageEnhance
from django.core.files import File

from django.core.mail import send_mail

image_types = {
    "jpg": "JPEG",
    "jpeg": "JPEG",
    "png": "PNG",
    "gif": "GIF",
    "tif": "TIFF",
    "tiff": "TIFF",
}


def render_image_with_watermark(photo):
    add_watermark(photo, 'media/logo/logo.png', opacity=0.5)


def add_watermark(image_file, logo_file, opacity=1.0):
    with Image.open(image_file, 'r').convert("RGB") as img:
        img.thumbnail((300, 450))
        img_filename = os.path.basename(image_file.url)
        img_suffix = Path(image_file.file.name).name.split(".")[-1]
        img_format = image_types[img_suffix]

        logo = Image.open(logo_file).convert("RGBA")

        base_w = (img.size[0]/4) / float(logo.size[0])
        base_h = (img.size[1]/6) / float(logo.size[1])

        logo_w = int(float(logo.size[0]) * float(base_w))
        logo_h = logo_w

        logo = logo.resize((logo_w, logo_h), Image.ANTIALIAS)

        # position the watermark
        offset_x = (img.size[0] - logo.size[0]) - 10
        offset_y = (img.size[1] - logo.size[1]) - 10

        watermark = logo.convert('RGBA')
        layer = Image.new('RGBA', img.size, (0, 0, 0, 0))
        alpha = watermark.split()[3]
        alpha = ImageEnhance.Brightness(alpha).enhance(opacity)
        watermark.putalpha(alpha)
        layer.paste(watermark, (offset_x, offset_y))
        img2 = Image.composite(layer, img, layer)

        buffer = BytesIO()
        img2.save(buffer, format=img_format)
        file_object = File(buffer)
        image_file.delete(save=False)
        image_file.save(img_filename, file_object)


def send_like_mail(message, mail_from, mail_to):
    send_mail(
        'Письмо дружбы',
        message,
        mail_from,
        [mail_to],
        fail_silently=False,
    )


def get_distance_between_users(lon1, lat1, lon2, lat2):
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    length = 2 * asin(sqrt(sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2))
    km = 6371 * length
    return round(km, 3)
