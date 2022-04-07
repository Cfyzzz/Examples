package ru.nedovizin;

import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        final String BASE_ADDR = "https://dimonvideo.ru/";
        Domen domen = Domen.getSite(BASE_ADDR);
        new ForkJoinPool(4).invoke(domen);
    }
}
