package ru.nedovizin.homeaccountancy;

import java.util.Arrays;
import java.util.List;

import ru.nedovizin.homeaccountancy.database.BaseLab;
import ru.nedovizin.homeaccountancy.models.Category;
import ru.nedovizin.homeaccountancy.models.TypeOperation;

public class CategoriesPool {
    private static List<Category> mCategories;
    private static BaseLab mBaseLab;

    public static void Create(TypeOperation typeOperation) {
        mBaseLab = BaseLab.get(null);
        loadCategories(typeOperation);
    }

    private static void loadCategories(TypeOperation typeOperation) {
        mCategories = mBaseLab.getAllCategories(typeOperation);
        if (mCategories != null) {
            TypeOperation expose = TypeOperation.EXPOSE;
            TypeOperation income = TypeOperation.INCOME;
            mCategories = Arrays.asList(
                    getInstanceCategory("Дом", expose),
                    getInstanceCategory("Дети", expose),
                    getInstanceCategory("Авто", expose),
                    getInstanceCategory("Продукты", expose),
                    getInstanceCategory("Зарплата", income)
            );
        }
    }

    private static Category getInstanceCategory(String name, TypeOperation type) {
        Category category = mBaseLab.getCategoryByName(name);
        if (category == null) {
            category = new Category(name, type);
            mBaseLab.addCategory(category);
        }
        return category;
    }
}
