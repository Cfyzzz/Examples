package ru.nedovizin.homeaccountancy.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ru.nedovizin.homeaccountancy.Period;
import ru.nedovizin.homeaccountancy.models.Category;
import ru.nedovizin.homeaccountancy.models.Operation;

public class OperationCursorWrapper extends CursorWrapper {
    public OperationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Operation getOperation() {
        int value = getInt(getColumnIndex(DbScheme.OperationTable.Cols.VALUE));
        String periodLine = getString(getColumnIndex(DbScheme.OperationTable.Cols.PERIOD));
        Period period = new Period(periodLine);
        int categoryId = getInt(getColumnIndex(DbScheme.OperationTable.Cols.CATEGORY));
        Category category = Category.getById(categoryId);

        Operation operation = new Operation();
        operation.setCategory(category);
        operation.setValue(value);
        operation.setPeriod(period);
        return operation;
    }
}
