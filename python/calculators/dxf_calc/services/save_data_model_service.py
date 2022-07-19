from dxf_calc import repository


def save_data_model(data: dict):
    if data is not None:
        repository.save_data_model(data)
