import pymorphy2
from sum_prop_lib.num2t4ru import num2text


def sum_prop(numb: int, gender: str, case: str):
    gender_out = 'f'
    gender = gender.upper()
    if gender == 'М':
        gender_out = 'm'
    elif gender == 'Ж':
        gender_out = 'f'
    elif gender == 'С':
        gender_out = 'n'
    units = ((u'', u'', u''), gender_out)
    sentence = num2text(num=numb, main_units=units)
    return _morpher_parser(sentence, case)


def _morpher_parser(text_numb: str, case: str):
    case = case.upper()
    case_out = 'nomn'
    if case == 'И':
        case_out = 'nomn'
    elif case == 'Р':
        case_out = 'gent'
    elif case == 'Д':
        case_out = 'datv'
    elif case == 'В':
        case_out = 'accs'
    elif case == 'Т':
        case_out = 'ablt'
    elif case == 'П':
        case_out = 'loct'

    morph = pymorphy2.MorphAnalyzer()
    words = text_numb.split(" ")
    result_words = []
    for word in words:
        word_parse = morph.parse(word)[0]
        word_inflect = word_parse.inflect({case_out}).word
        word_inflect = _fix_morpher(word=word_inflect, case=case)
        result_words.append(word_inflect)

    return f"{' '.join(result_words)}".strip()


def _fix_morpher(word: str, case: str):
    if word == 'сто' and case == 'Д':
        word = 'стам'
    elif word == 'сто' and case == 'Т':
        word = 'ста'
    return word
