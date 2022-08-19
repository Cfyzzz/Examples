from rest_framework import viewsets
from rest_framework import permissions

from api.models import Organization, CustomUser
from api.serializers import UserSerializer, OrganizationSerializer, CreateUserSerializer


class UserIsOwnerOrReadOnly(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True
        return obj.id == request.user.id


class UserViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = CustomUser.objects.all()
    serializer_class = UserSerializer
    permission_classes = [UserIsOwnerOrReadOnly]

    def create(self, request, *args, **kwargs):
        self.serializer_class = CreateUserSerializer
        self.permission_classes = (permissions.AllowAny,)
        return super(UserViewSet, self).create(request, *args, **kwargs)


class OrganizationViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows organization to be viewed or edited.
    """
    queryset = Organization.objects.all()
    serializer_class = OrganizationSerializer
    permission_classes = [permissions.IsAuthenticated]
