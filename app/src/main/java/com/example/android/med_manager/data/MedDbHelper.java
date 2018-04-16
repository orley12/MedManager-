package com.example.android.med_manager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.data.MedContract.ProfileEntry;

/**
 * Created by SOLARIN O. OLUBAYODE on 01/04/18.
 */

public class MedDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "medication.db";
    public static final String CREATE_PROFILE_ENTRIES = "CREATE TABLE " +
            ProfileEntry.PROFILE_TABLE_NAME + "( " +
            ProfileEntry.PROFILE_DB_DEFAULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProfileEntry.PROFILE_COLUMN_EMAIL + " TEXT DEFAULT ''," +
            ProfileEntry.PROFILE_COLUMN_NAME + " TEXT DEFAULT ''," +
            ProfileEntry.PROFILE_SURNAME_NAME + " TEXT DEFAULT ''," +
            ProfileEntry.PROFILE_ID_GOOGLE + " TEXT DEFAULT ''," +
            ProfileEntry.PROFILE_USER_NAME + " TEXT DEFAULT '' " + ")";
    public static final String CREATE_MEDICATION_ENTRIES = "CREATE TABLE " +
            MedEntry.MED_TABLE_NAME + "( " +
            MedEntry.MED_DB_DEFAULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MedEntry.MED_COLUMN_NAME + " TEXT," +
            MedEntry.MED_COLUMN_TYPE + " INTEGER," +
            MedEntry.MED_COLUMN_DESCRIPTION + " TEXT," +
            MedEntry.MED_COLUMN_INTERVAL + " INTEGER," +
            MedEntry.MED_COLUMN_DOSAGE + " INTEGER," +
            MedEntry.MED_COLUMN_START_DATE + " INTEGER," +
            MedEntry.MED_COLUMN_END_DATE + " INTEGER," +
            MedEntry.MED_COLUMN_TAKEN_COUNT + " INTEGER," +
            MedEntry.MED_COLUMN_IGNORE_COUNT + " INTEGER," +
            MedEntry.MED_COLUMN_REMINDER_COUNT + " INTEGER" + ")";
    public static final String SQL_DELETE_PROFILE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProfileEntry.PROFILE_TABLE_NAME;
    public static final String SQL_DELETE_MEDICATION_ENTRIES =
            "DROP TABLE IF EXISTS " + ProfileEntry.PROFILE_TABLE_NAME;
    private static final String TAG = MedDbHelper.class.getSimpleName();

    public MedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Execute the SQL statement
        sqLiteDatabase.execSQL(CREATE_PROFILE_ENTRIES);
        sqLiteDatabase.execSQL(CREATE_MEDICATION_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_PROFILE_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_MEDICATION_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
