package com.arcaneconstruct.cursbnr;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Archangel on 4/6/2016.
 */
public class ExchangeProvider extends ContentProvider {
    private ExchangeDatabase databaseHelper;
    private static final String TAG = "ExchangeProvider";
    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.arcaneconstruct.cursbnr.rates";
    public static final String CONTENT_TYPE_ITEM =
            "vnd.android.cursor.item/com.arcaneconstruct.cursbnr.rates";
    private static final UriMatcher uriMatcher;
    public static final Uri CONTENT_URI =
            Uri.parse("content://com.arcaneconstruct.cursbnr/rates");

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = "com.arcaneconstruct.cursbnr";
        uriMatcher.addURI("com.arcaneconstruct.cursbnr", "rates", ALLROWS);
        uriMatcher.addURI("com.arcaneconstruct.cursbnr", "rates/#", SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new ExchangeDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String groupBy = null;
        String having = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ExchangeDatabase.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(BaseColumns._ID + "=" + rowID);
            default:
                break;
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALLROWS:
                return CONTENT_TYPE;
            case SINGLE_ROW:
                return CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String nullColumnHack = null;
        Log.d(TAG, values.toString());
        long id = db.insert(ExchangeDatabase.TABLE_NAME,
                nullColumnHack, values);
        if (id > -1) {
//returnam URI pentru elementul adaugat
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
//daca avem observatori asociati ii notificam ca au fost adaugate date
            getContext().getContentResolver().notifyChange(insertedId, null);
            return insertedId;
        } else
            return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = BaseColumns._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            default:
                break;
        }
        if (selection == null)
            selection = "1";
        int deleteCount = db.delete(ExchangeDatabase.TABLE_NAME,
                selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = BaseColumns._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
            default:
                break;
        }
        int updateCount = db.update(ExchangeDatabase.TABLE_NAME,
                values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
