package ru.nedovizin.homeaccountancy.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import ru.nedovizin.homeaccountancy.models.NameCategory;
import ru.nedovizin.homeaccountancy.models.TypeOperation;

public class CategoryNameCursorWrapper extends CursorWrapper {
    private final String TAG = ".CategoryNameCursorWrapper";

    public CategoryNameCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public NameCategory getNameCategory() {
        String name = getString(getColumnIndex(DbScheme.CategoryNameTable.Cols.NAME));
        String typeStr = getString(getColumnIndex(DbScheme.CategoryNameTable.Cols.TYPE));
        int id = getInt(getColumnIndex(DbScheme.CategoryNameTable.Cols.ID));
        TypeOperation type = TypeOperation.valueOf(typeStr);

        NameCategory nameCategory = new NameCategory(name, type);
        nameCategory.setId(id);
        return nameCategory;
    }
}
