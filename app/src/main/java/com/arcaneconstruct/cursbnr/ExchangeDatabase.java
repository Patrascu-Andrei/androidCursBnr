package com.arcaneconstruct.cursbnr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class ExchangeDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExchangeDatabase";
    public static final String TABLE_NAME = "RATES";
    public static final String COLUMN_RATE = "RATE";
    public static final String COLUMN_CURRENCY = "CURRENCY";
    public static final String COLUMN_DATE = "DATE";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public ExchangeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "("
                        + BaseColumns._ID + " INTEGER PRIMARY_KEY , "
                        + COLUMN_CURRENCY + " TEXT NOT NULL, "
                        + COLUMN_DATE + " TEXT NOT NULL, "
                        + COLUMN_RATE + " TEXT NOT NULL);"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
