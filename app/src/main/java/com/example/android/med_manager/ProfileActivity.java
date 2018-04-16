package com.example.android.med_manager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.example.android.med_manager.data.MedContract.ProfileEntry;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();
    EditText mNameEditText;

    EditText mSurnameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameEditText = findViewById(R.id.first_name);

        mSurnameEditText = findViewById(R.id.last_name);
        dbQueryForUsersData();
    }

    private void dbQueryForUsersData() {
//        SQLiteDatabase db = medDbHelper.getReadableDatabase();
//
//        String[] projection = {
//                ProfileEntry.PROFILE_DB_DEFAULT_ID,
//                ProfileEntry.PROFILE_USER_NAME,
//                ProfileEntry.PROFILE_COLUMN_EMAIL,
//                ProfileEntry.PROFILE_SURNAME_NAME,
//                ProfileEntry.PROFILE_COLUMN_NAME
//        };
//
//        Cursor cursor = db.query(
//                ProfileEntry.PROFILE_TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null);
//
//        while (cursor.moveToNext()){
//            String email = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_COLUMN_EMAIL));
//            Log.e(LOG_TAG,"LOOK HERE"+ email);
//            mNameEditText.setText(email);
//        }
        String[] projection = new String[]{
                ProfileEntry.PROFILE_DB_DEFAULT_ID,
                ProfileEntry.PROFILE_USER_NAME,
                ProfileEntry.PROFILE_COLUMN_EMAIL,
                ProfileEntry.PROFILE_SURNAME_NAME,
                ProfileEntry.PROFILE_COLUMN_NAME,
                ProfileEntry.PROFILE_ID_GOOGLE
        };

        Cursor cursor = getContentResolver().query(ProfileEntry.CONTENT_URI, projection, null
                , null, null);
        String name = null;
        String surname = null;
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_COLUMN_NAME));
            surname = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_SURNAME_NAME));
            Log.e(LOG_TAG, "LOOK HERE" + name);
        }
        mNameEditText.setText(name);
        mSurnameEditText.setText(surname);

    }

}
