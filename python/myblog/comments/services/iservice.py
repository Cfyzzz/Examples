class IService:
    def __init__(self, the_request, **params):
        self.request = the_request
        self.params = params

    def run(self):
        ...
