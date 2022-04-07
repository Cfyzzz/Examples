package ru.nedovizin;

import java.util.Arrays;

public class Probe {

    public static void main(String[] args) {
        Arrays.stream(decryptData( new int[]{5,100,20,66,16}, 50, 1, 3))
                .forEach(System.out::println);
    }

    /**
     * Метод "скидка". Применяет скидку discount к цене price, начиная с позиции offset
     * на количество позиций readLength. Новые цены округляем “вниз”,
     * до меньшего целого числа.
     * @param price - исходные цены.
     * @param discount - % скидки, от 1 до 99.
     * @param offset - номер позиции, с которой нужно применить скидку.
     * @param readLength - количество позиций, к которым нужно применить скидку.
     * @return - массив новых цен.
     */
    public static int[] decryptData(int[] price,
                             int discount,
                             int offset,
                             int readLength) {
        if (price.length < offset + readLength || discount < 1 || discount > 99) {
            throw new IllegalArgumentException();
        }

        int[] result = new int[readLength];
        for (int positionDest = 0; positionDest < readLength; positionDest++) {
            result[positionDest] = price[positionDest + offset] * (100 - discount) / 100;
        }
        return result;
    }
}
