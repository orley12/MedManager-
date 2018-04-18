package com.example.android.med_manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.med_manager.data.MedContract.ProfileEntry;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();
    EditText mNameEditText;

    EditText mSurnameEditText;

    EditText mUsernameEditText;

    EditText mEmailEditText;

    TextView mMainNameEditText;

    TextView mAbbreviatedNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameEditText = findViewById(R.id.first_name);

        mSurnameEditText = findViewById(R.id.last_name);

        mUsernameEditText = findViewById(R.id.user_name);

        mEmailEditText = findViewById(R.id.email);

        mMainNameEditText = findViewById(R.id.main_name);

        mAbbreviatedNameTextView = findViewById(R.id.abbreviated_name);

        dbQueryForUsersData();
    }

    private void dbQueryForUsersData() {

        String[] projection = new String[]{
                ProfileEntry.PROFILE_DB_DEFAULT_ID,
                ProfileEntry.PROFILE_USER_NAME,
                ProfileEntry.PROFILE_COLUMN_EMAIL,
                ProfileEntry.PROFILE_SURNAME_NAME,
                ProfileEntry.PROFILE_COLUMN_NAME,
                ProfileEntry.PROFILE_ID_GOOGLE
        };
        String selection = ProfileEntry.PROFILE_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(1)};
        Cursor cursor = getContentResolver().query(ProfileEntry.CONTENT_URI, projection, selection,
                selectionArgs, null);
        String name = null;
        String surname = null;
        String username = null;
        String email = null;
        String googleId = null;
        int count = 0;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_COLUMN_NAME));
            surname = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_SURNAME_NAME));
            username = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_USER_NAME));
            email = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_COLUMN_EMAIL));
            googleId = cursor.getString(cursor.getColumnIndexOrThrow(ProfileEntry.PROFILE_ID_GOOGLE));
        }
        mNameEditText.setText(name);
        mSurnameEditText.setText(surname);
        mUsernameEditText.setText(username);
        mEmailEditText.setText(email);
        mMainNameEditText.setText(name);
        mAbbreviatedNameTextView.setText(name.substring(0,1) + surname.substring(0,1));
    }

    public void dbInsertForUsersData() {
        String name = mNameEditText.getText().toString().trim();
        String surname = mSurnameEditText.getText().toString().trim();
        String username = mUsernameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("surname",surname);
        contentValues.put("username",username);
        contentValues.put("email",email);

        String selection = ProfileEntry.PROFILE_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(1)};
        getContentResolver().update(ProfileEntry.CONTENT_URI,contentValues,selection,selectionArgs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionSelectedId = item.getItemId();
        switch (optionSelectedId) {
            case R.id.action_save:
               dbInsertForUsersData();
               finish();
                return true;
            case R.id.action_delete:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
