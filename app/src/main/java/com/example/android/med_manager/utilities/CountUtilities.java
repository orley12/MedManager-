package com.example.android.med_manager.utilities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.med_manager.data.MedContract.MedEntry;

/**
 * Created by SOLARIN O. OLUBAYODE on 25/03/18.
 */

public class CountUtilities {
    public static final String TAG="CountUtilities";


    public static Cursor getTakenCount(Context context, long id) {

        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(
                MedEntry.CONTENT_URI, id), null, null,
                null, null);
        return cursor;
    }

    public static void incrementTakenCount(Context context, long id) {
        Cursor gottenCursor = CountUtilities.getTakenCount(context, id);
        int takenCount = 0;

        gottenCursor.moveToFirst();
            takenCount = gottenCursor.getInt(gottenCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TAKEN_COUNT));

        setTakenCount(context, id, ++takenCount);
        Log.i(TAG,"TAKEN COUNT :" + takenCount);

        gottenCursor.close();
    }

    private static void setTakenCount(Context context, long id, int medTaken) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MedEntry.MED_COLUMN_TAKEN_COUNT, medTaken);

        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        context.getContentResolver().update(MedEntry.CONTENT_URI, contentValues, selection,
                selectionArgs);
    }

    public static Cursor getIgnoreCount(Context context, long id) {

        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(
                MedEntry.CONTENT_URI, id), null, null,
                null, null);
        return cursor;
    }

    public static void incrementIgnoreCount(Context context, long id) {
        Cursor ignoreGottenCursor = CountUtilities.getIgnoreCount(context, id);
        int ignoreCount = 0;
        ignoreGottenCursor.moveToFirst();
            ignoreCount = ignoreGottenCursor.getInt(ignoreGottenCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_IGNORE_COUNT));
            Log.i(TAG,"IGNORE COUNT :" + ignoreCount);

        setIgnoreCount(context, id, ++ignoreCount);
        ignoreGottenCursor.close();
    }

    private static void setIgnoreCount(Context context, long id, int medTaken) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MedEntry.MED_COLUMN_IGNORE_COUNT, medTaken);

        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        context.getContentResolver().update(MedEntry.CONTENT_URI, contentValues, selection,
                selectionArgs);
    }

    public static void incrementMedReminderCount(Context context, long id) {
        Cursor gottenReminderCount = CountUtilities.getMedReminderCount(context, id);
        int reminderCount = 0;
        gottenReminderCount.moveToFirst();
            reminderCount = gottenReminderCount.getInt(gottenReminderCount.getColumnIndexOrThrow(MedEntry.MED_COLUMN_REMINDER_COUNT));

        ContentValues contentValues = new ContentValues();
        contentValues.put(MedEntry.MED_COLUMN_REMINDER_COUNT, reminderCount);

        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        context.getContentResolver().update(MedEntry.CONTENT_URI, contentValues, selection,
                selectionArgs);
    }

    public static Cursor getMedReminderCount(Context context, long id) {

        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(
                MedEntry.CONTENT_URI, id), null, null,
                null, null);
        cursor.close();
        return cursor;
    }
}
