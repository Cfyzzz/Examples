package ru.nedovizin;

import java.io.IOException;

public class Lematizator {
    public static void main(String[] args) throws IOException {
        var result = MyLematizator.getWordsBaseForms("Повторное не появление леопарда в Осетии позволяет предположить, что леопард постоянно обитает в некоторых районах Северного Кавказа. Попробуйте передать на вход программы несколько разных текстов и проверьте, верно ли выдаётся список лемм с количествами.");
        result.forEach(w->{
            System.out.println(w);
        });
    }
}
