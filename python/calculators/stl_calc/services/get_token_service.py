import secrets


def get_new_token() -> str:
    """
    Генерирует новый токен

    :return: новый токен
    """
    return secrets.token_hex(nbytes=12)
