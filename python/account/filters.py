import django_filters

from .utils import get_distance_between_users
from .models import Profile


class ProfileFilter(django_filters.FilterSet):
    class Meta:
        model = Profile
        fields = ['sex', 'user__first_name', 'user__last_name']


class DistanceFilter(django_filters.FilterSet):

    def __init__(self, request, queryset, distance):
        self.request2 = request
        self.distance = int(distance)
        super().__init__(request, queryset)

    def filter_queryset(self, queryset):
        all_profiles = queryset
        new_queryset = []
        im_user = self.request2.user
        try:
            me_profile = Profile.objects.get(user=im_user)
        except Exception:
            return queryset

        for profile in all_profiles:
            current_user_long = me_profile.longitude
            current_user_lat = me_profile.latitude
            alluser_long = profile.longitude
            alluser_lat = profile.latitude
            distance_calc = get_distance_between_users(current_user_long, current_user_lat, alluser_long, alluser_lat)
            if self.distance > distance_calc:
                new_queryset.append(profile)
        return new_queryset
