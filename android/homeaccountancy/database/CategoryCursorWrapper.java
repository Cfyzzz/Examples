package ru.nedovizin.homeaccountancy.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.homeaccountancy.Period;
import ru.nedovizin.homeaccountancy.models.Category;
import ru.nedovizin.homeaccountancy.models.NameCategory;
import ru.nedovizin.homeaccountancy.models.TypeOperation;

public class CategoryCursorWrapper extends CursorWrapper {
    public CategoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Category getCategory() {
        int id = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.ID));
        int pririty = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.PRIORITY));
        int categoryNameId = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.CATEGORY_NAME));
        String typeStr = getString(getColumnIndex(DbScheme.CategoryTable.Cols.TYPE));
        String period = getString(getColumnIndex(DbScheme.CategoryTable.Cols.PERIOD));
        int color = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.COLOR));
        int exposed = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.EXPOSED));
        int reserved = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.RESERVED));
        int planned = getInt(getColumnIndex(DbScheme.CategoryTable.Cols.PLANNED));

        NameCategory nameCategory = NameCategory.getById(categoryNameId);
        TypeOperation type = TypeOperation.valueOf(typeStr);

        Category category = new Category(nameCategory, type);
        category.setId(id);
        category.setPriority(pririty);
        category.setPeriod(new Period(period));
        category.setColor(color);
        category.setExposed(exposed);
        category.setReserved(reserved);
        category.setPlanned(planned);
        return category;
    }
}
