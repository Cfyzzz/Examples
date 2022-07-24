from sum_prop_lib import sum_prop


def decor_print(func):
    def wrap(numb: int, gender: str, case: str):
        print(f"{gender} {case}: ", end="")
        result = func(numb, gender, case)
        print(result)
        return result
    return wrap


@decor_print
def my_sum_prop(*args):
    return sum_prop(*args)


if __name__ == "__main__":
    my_sum_prop(31, "М", "Р")
    my_sum_prop(22, "С", "Т")
    my_sum_prop(154323, "М", "И")
    my_sum_prop(154323, "М", "Т")
