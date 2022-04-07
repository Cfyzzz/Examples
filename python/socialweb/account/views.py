from django.contrib import messages
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.http import Http404
from django.shortcuts import render

from .filters import ProfileFilter, DistanceFilter
from .forms import UserRegistrationForm, UserEditForm, ProfileEditForm
from .models import Profile, Friend
from .utils import render_image_with_watermark, send_like_mail


@login_required
def dashboard(request):
    return render(request, 'account/dashboard.html', {'section': 'dashboard'})


def register(request):
    if request.method == 'POST':
        user_form = UserRegistrationForm(request.POST)
        if user_form.is_valid():
            new_user = user_form.save(commit=False)
            new_user.set_password(user_form.cleaned_data['password'])
            new_user.save()
            Profile.objects.create(user=new_user)
            return render(request, 'account/register_done.html', {'new_user': new_user})
    else:
        user_form = UserRegistrationForm()
    return render(request, 'account/register.html', {'user_form': user_form})


@login_required
def edit(request):
    if request.method == 'POST':
        user_form = UserEditForm(instance=request.user, data=request.POST)
        profile_form = ProfileEditForm(instance=request.user.profile, data=request.POST, files=request.FILES)
        if user_form.is_valid() and profile_form.is_valid():
            user_form.save()
            profile_form.save()
            if request.user.profile.photo:
                # TODO - Надо накладывать водяной знак один раз или не менять исходное изображение
                render_image_with_watermark(request.user.profile.photo)
            messages.success(request, 'Profile updated successfully')
        else:
            messages.error(request, 'Error updating your profile')
    else:
        user_form = UserEditForm(instance=request.user)
        profile_form = ProfileEditForm(instance=request.user.profile)
    return render(request,
                  'account/edit.html',
                  {'user_form': user_form,
                   'profile_form': profile_form})


@login_required
def match(request, friend_id):
    try:
        user_friend = User.objects.get(id=friend_id)
    except Exception:
        raise Http404("Пользователь не найден!")

    confirmed = False
    invited = False
    if request.method == 'POST':
        if int(friend_id) != request.user.id:
            friend, _ = Friend.objects.get_or_create(user=request.user, users_friend=user_friend)
            friend.confirmed = Friend.objects.filter(user=user_friend, users_friend=request.user).exists()
            user_friend.confirmed = friend.confirmed
            friend.save()
            user_friend.save()
            if friend.confirmed:
                send_like_mail(message=f"Вы понравились {{{user_friend.first_name}}}! "
                                       f"Почта участника: {{{user_friend.email}}}",
                               mail_from=request.user.email,
                               mail_to=user_friend.email
                               )

    try:
        friend = Friend.objects.get(user=request.user, users_friend=user_friend)
        confirmed = friend.confirmed
        invited = True
    except Exception:
        pass

    return render(request, 'account/my_friend.html', {
        'section': 'people',
        'user_friend': user_friend,
        'confirmed': confirmed,
        'invited': invited,
    })


@login_required
def list_members(request):
    f_profile = ProfileFilter(request.GET, queryset=Profile.objects.all().exclude(user=request.user))
    return render(request,
                  'account/list.html',
                  {
                      'section': 'people',
                      'filter_profile': f_profile
                  })


@login_required
def list_members_into_distance(request, distance):
    try:
        user_profile = Profile.objects.get(user=request.user)
    except Exception:
        raise Http404("Профиль пользователя не найден!")

    f_profile = DistanceFilter(request,
                               queryset=Profile.objects.all().exclude(user=request.user),
                               distance=distance
                               )
    return render(request,
                  'account/list.html',
                  {
                      'section': 'people',
                      'filter_profile': f_profile
                  })
