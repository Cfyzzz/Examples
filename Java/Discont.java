package ru.nedovizin.testapp;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Discont {

    /**
     * Метод "скидка". Применяет скидку discount к цене price, начиная с позиции offset
     * на количество позиций readLength. Новые цены округляем “вниз”,
     * до меньшего целого числа.
     *
     * @param price      - исходные цены.
     * @param discount   - % скидки, от 1 до 99.
     * @param offset     - номер позиции, с которой нужно применить скидку.
     * @param readLength - количество позиций, к которым нужно применить скидку.
     * @return - массив новых цен.
     */
    public @Nullable
    int[] decryptData(@NonNull int[] price,
                      @IntRange(from = 1) int discount,
                      @IntRange(from = 0) int offset,
                      @IntRange(from = 1) int readLength) {
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
