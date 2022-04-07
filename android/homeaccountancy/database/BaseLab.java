package ru.nedovizin.homeaccountancy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.nedovizin.homeaccountancy.Period;
import ru.nedovizin.homeaccountancy.database.DbScheme.CategoryNameTable;
import ru.nedovizin.homeaccountancy.database.DbScheme.CategoryTable;
import ru.nedovizin.homeaccountancy.database.DbScheme.OperationTable;
import ru.nedovizin.homeaccountancy.models.Category;
import ru.nedovizin.homeaccountancy.models.NameCategory;
import ru.nedovizin.homeaccountancy.models.Operation;
import ru.nedovizin.homeaccountancy.models.TypeOperation;

public class BaseLab {
    private static BaseLab sBaseLab;
    private final SQLiteDatabase mDatabase;
    private final static String TAG = ".BaseLab";

    private BaseLab(Context context) {
        mDatabase = BaseHelper
                .getInstance(context.getApplicationContext())
                .getWritableDatabase();
    }

    public static BaseLab get(Context context) {
        if (sBaseLab == null) {
            sBaseLab = new BaseLab(context);
        }
        return sBaseLab;
    }

    /**
     * Преобразовать дату в строку по единому правилу
     *
     * @param date Дата
     * @return Строка с датой в едином формате
     */
    public String DateToString(Date date) {
        return DateFormat.format("yyyy-MM-ddThh:mm:ss", date).toString();
    }

    public List<Category> getAllCategories(TypeOperation typeOperation) {
        List<Category> categories = new ArrayList<>();
        try (CategoryNameCursorWrapper cursor = queryCategoryName(
                CategoryNameTable.Cols.TYPE + " = ?",
                new String[]{String.valueOf(typeOperation)}
        )
        ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Category category = new Category(cursor.getNameCategory().getName(), typeOperation);
                categories.add(category);
                cursor.moveToNext();
            }
        }
        return categories;
    }

    public List<NameCategory> getListNameCategory() {
        List<NameCategory> nameCategoryList = new ArrayList<>();
        try (CategoryNameCursorWrapper cursor = queryCategoryName(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                nameCategoryList.add(cursor.getNameCategory());
                cursor.moveToNext();
            }
        }
        return nameCategoryList;
    }

    public NameCategory getNameCategoryByName(String name) {
        try (CategoryNameCursorWrapper cursor = queryCategoryName(CategoryNameTable.Cols.NAME + " = ?", new String[]{name})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNameCategory();
        }
    }

    public NameCategory getNameCategoryById(int id) {
        try (CategoryNameCursorWrapper cursor = queryCategoryName(CategoryNameTable.Cols.ID + " = ?", new String[]{String.valueOf(id)})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNameCategory();
        }
    }

    public void addNameCategory(String name, TypeOperation type) {
        NameCategory nameCategory = new NameCategory(name, type);
        ContentValues values = getCategoryNameValues(nameCategory);
        mDatabase.insert(CategoryNameTable.NAME, null, values);
    }

    public Category getCategoryByName(String name) {
        NameCategory nameCategory = getNameCategoryByName(name);
        if (nameCategory == null) {
            return null;
        }
        try (CategoryCursorWrapper cursor = queryCategory(
                CategoryTable.Cols.CATEGORY_NAME + " = ?",
                new String[]{String.valueOf(nameCategory.getId())})
        ) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCategory();
        }
    }

    public Category getCategoryById(int id) {
        try (CategoryCursorWrapper cursor = queryCategory(
                CategoryTable.Cols.ID + " = ?",
                new String[]{String.valueOf(id)})
        ) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCategory();
        }
    }

    public void addCategory(Category category) {
        ContentValues values = getCategoryValues(category);
        mDatabase.insert(CategoryTable.NAME, null, values);
    }

    public void addOperation(Operation operation) {
        if (operation == null) {
            return;
        }
        ContentValues values = getOperationValues(operation);
        mDatabase.insert(OperationTable.NAME, null, values);
    }

    public List<Operation> getOperationByPeriod(Period period) {
        List<Operation> operations = new ArrayList<>();
        try (OperationCursorWrapper cursor = queryOperation(
                new String[]{OperationTable.Cols.PERIOD, OperationTable.Cols.CATEGORY, "SUM(" + OperationTable.Cols.VALUE + ") " + OperationTable.Cols.VALUE},
                OperationTable.Cols.PERIOD + " = ?",
                new String[]{period.getFormatLine()},
                OperationTable.Cols.PERIOD + ", " + OperationTable.Cols.CATEGORY
        )
        ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                operations.add(cursor.getOperation());
                cursor.moveToNext();
            }
        }
        return operations;
    }

    private CategoryNameCursorWrapper queryCategoryName(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CategoryNameTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CategoryNameCursorWrapper(cursor);
    }

    private CategoryCursorWrapper queryCategory(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CategoryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CategoryCursorWrapper(cursor);
    }

    private OperationCursorWrapper queryOperation(String[] columns, String whereClause, String[] whereArgs, String groupBy) {
        Cursor cursor = mDatabase.query(
                OperationTable.NAME,
                columns,
                whereClause,
                whereArgs,
                groupBy,
                null,
                null
        );
        return new OperationCursorWrapper(cursor);
    }

    private static ContentValues getCategoryNameValues(NameCategory nameCategory) {
        ContentValues values = new ContentValues();
        Log.d(TAG, "NameCategory: name=" + nameCategory.getName() + "; type=" + nameCategory.getType());
        values.put(CategoryNameTable.Cols.NAME, nameCategory.getName());
        values.put(CategoryNameTable.Cols.TYPE, nameCategory.getType().name());
//        values.put(CategoryNameTable.Cols.ID, nameCategory.getId());
        return values;
    }

    private static ContentValues getCategoryValues(Category category) {
        ContentValues values = new ContentValues();
        values.put(CategoryTable.Cols.CATEGORY_NAME, category.getNameCategory().getId());
        values.put(CategoryTable.Cols.COLOR, category.getColor());
//        values.put(CategoryTable.Cols.ID, category.getId());
        values.put(CategoryTable.Cols.TYPE, category.getType().name());
        values.put(CategoryTable.Cols.PRIORITY, category.getPriority());
        values.put(CategoryTable.Cols.EXPOSED, category.getExposed());
        values.put(CategoryTable.Cols.PLANNED, category.getPlanned());
        values.put(CategoryTable.Cols.RESERVED, category.getReserved());
        return values;
    }

    private static ContentValues getOperationValues(Operation operation) {
        Log.d(TAG, "ContentValues value, categoryId, period VALUES(" +
                operation.getValue() + ", " +
                operation.getCategory().getId() + ", " +
                operation.getPeriod().getFormatLine() +
                ")"
        );
        ContentValues values = new ContentValues();
        values.put(OperationTable.Cols.VALUE, operation.getValue());
        values.put(OperationTable.Cols.CATEGORY, operation.getCategory().getId());
        values.put(OperationTable.Cols.PERIOD, operation.getPeriod().getFormatLine());
        return values;
    }
}
