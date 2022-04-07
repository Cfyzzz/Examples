package ru.nedovizin.homeaccountancy;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import androidx.core.graphics.ColorUtils;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ru.nedovizin.homeaccountancy.models.Category;
import ru.nedovizin.homeaccountancy.models.Operation;

public class CategoryViewModel extends BaseObservable {
    private Category mCategory;
    private Operation mOperation;
    private final String TAG = ".CategoryViewModel";

    public CategoryViewModel(Operation operation) {
        mOperation = operation;
    }

    @Bindable
    public String getTitle() {
        return mCategory.getName();
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
        notifyChange();
    }

    public void onButtonClicked() {
        Log.d(TAG, "onButtonClicked: category.name=" + mCategory.getName());
        mOperation.setCategory(mCategory);
        notifyChange();
        // TODO: 25.02.2022 Сделать select кнопки (визуальная часть)
    }
}
// TODO: 24.02.2022 Создать карточку и поместить в список выбора категорий операции
//  Стр 582 учебника
