package com.example.android.med_manager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.data.MedContract.ProfileEntry;

/**
 * Created by SOLARIN O. OLUBAYODE on 01/04/18.
 */

public class MedProvider extends ContentProvider {

    public static final String LOG_TAG = MedProvider.class.getName();
    public static final int ENTIRE_PROFILE_TABLE = 100;
    public static final int ENTIRE_MEDICATION_TABLE = 200;
    public static final int MEDICATION_TABLE_WITH_ID = 202;
    public static final UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUrimatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PROFILE_PATH, ENTIRE_PROFILE_TABLE);
        sUrimatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.MEDICATION_PATH, ENTIRE_MEDICATION_TABLE);
        sUrimatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.MEDICATION_PATH + "/#", MEDICATION_TABLE_WITH_ID);
    }

    private MedDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MedDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUrimatcher.match(uri);
        switch (match) {
            case ENTIRE_PROFILE_TABLE:
                cursor = database.query(ProfileEntry.PROFILE_TABLE_NAME, projection,
                        null, null, null, null, null);
                break;
            case ENTIRE_MEDICATION_TABLE:
                cursor = database.query(MedEntry.MED_TABLE_NAME, projection,
                        null, null, null, null, null);
                break;
            case MEDICATION_TABLE_WITH_ID:
                selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MedEntry.MED_TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown Uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUrimatcher.match(uri);
        Uri resultUri = null;
        switch (match) {
            case ENTIRE_PROFILE_TABLE:
                resultUri = insertProfileData(uri, contentValues);
                break;
            case ENTIRE_MEDICATION_TABLE:
                resultUri = insertMedData(uri, contentValues);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    private Uri insertProfileData(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(ProfileEntry.PROFILE_TABLE_NAME,
                null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row at" + id);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertMedData(Uri uri, ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(MedEntry.MED_TABLE_NAME,
                null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row at" + id);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        int match = sUrimatcher.match(uri);
        int numberOfRowsDeleted = 0;
        switch (match) {
            case ENTIRE_PROFILE_TABLE:
                numberOfRowsDeleted = sqLiteDatabase.delete(ProfileEntry.PROFILE_TABLE_NAME,
                        selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numberOfRowsDeleted;
            case ENTIRE_MEDICATION_TABLE:
                numberOfRowsDeleted = sqLiteDatabase.delete(MedEntry.MED_TABLE_NAME,
                        selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numberOfRowsDeleted;
            case MEDICATION_TABLE_WITH_ID:
                selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numberOfRowsDeleted = sqLiteDatabase.delete(MedEntry.MED_TABLE_NAME,
                        selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numberOfRowsDeleted;
            default:
                throw new IllegalArgumentException("cannot delete unknown Uri" + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUrimatcher.match(uri);
        int updatedRow;
        switch (match) {
            case ENTIRE_PROFILE_TABLE:
                updatedRow = database.update(ProfileEntry.PROFILE_TABLE_NAME,
                        values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return updatedRow;
            case ENTIRE_MEDICATION_TABLE:
                updatedRow = database.update(MedEntry.MED_TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return updatedRow;
            case MEDICATION_TABLE_WITH_ID:
                selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRow = database.update(MedEntry.MED_TABLE_NAME, values,
                        selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return updatedRow;

            default:
                throw new IllegalArgumentException("Editing is not supported for :" + uri);
        }
    }
}
