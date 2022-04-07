package ru.nedovizin.homeaccountancy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.nedovizin.homeaccountancy.database.DbScheme.CategoryNameTable;
import ru.nedovizin.homeaccountancy.database.DbScheme.CategoryTable;
import ru.nedovizin.homeaccountancy.database.DbScheme.OperationTable;

public class BaseHelper extends SQLiteOpenHelper {
    private static BaseHelper sInstance;
    public static final int VERSION = 6;
    public static final String DATABASE_NAME = "homeAccountancy.db";

    public static synchronized BaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_NAME_TABLE = "CREATE TABLE " + CategoryNameTable.NAME +
                "(" +
                CategoryNameTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CategoryNameTable.Cols.NAME + " TEXT(200)," +
                CategoryNameTable.Cols.TYPE + " TEXT(20)," +
                " unique(" + CategoryNameTable.Cols.NAME + ") ON CONFLICT replace)";

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryTable.NAME +
                "(" +
                CategoryTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CategoryTable.Cols.PRIORITY + " INTEGER, " +
                CategoryTable.Cols.PERIOD + " TEXT(10), " +
                CategoryTable.Cols.TYPE + " TEXT(20), " +
                CategoryTable.Cols.EXPOSED + " INTEGER, " +
                CategoryTable.Cols.RESERVED + " INTEGER, " +
                CategoryTable.Cols.PLANNED + " INTEGER, " +
                CategoryTable.Cols.CATEGORY_NAME + " INTEGER REFERENCES " + CategoryNameTable.NAME + "," +
                CategoryTable.Cols.COLOR + " INTEGER " +
                ")";

        String CREATE_OPERATION = "CREATE TABLE " + OperationTable.NAME +
                "(" +
                OperationTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                OperationTable.Cols.CATEGORY + " INTEGER REFERENCES " + CategoryTable.NAME + "," +
                OperationTable.Cols.VALUE + " INTEGER, " +
                OperationTable.Cols.PERIOD + " TEXT(10) " +
                ")";

        db.execSQL(CREATE_CATEGORY_NAME_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_OPERATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
//            db.execSQL("ALTER TABLE DRINK ADD COLUMN FAVORITE NUMERIC;");
        }
        if (oldVersion != newVersion) {
//            db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.NAME);
//            db.execSQL("DROP TABLE IF EXISTS " + CategoryNameTable.NAME);
//            db.execSQL("DROP TABLE IF EXISTS " + OperationTable.NAME);
//            onCreate(db);
        }
    }
}
