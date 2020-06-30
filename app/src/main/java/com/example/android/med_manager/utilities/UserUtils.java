package com.example.android.med_manager.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.android.med_manager.model.User;

import java.util.regex.Pattern;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.CONTENT_URI;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_EMAIL;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_FIRST_NAME;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_LAST_NAME;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_PASSWORD;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_USER_NAME;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_USER_PHOTO_URI;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_DB_DEFAULT_ID;
import static com.example.android.med_manager.utilities.LoginUtils.getUserDataFormDataBase;

public class UserUtils {

    public static User getUserObject(Cursor cursor){

        cursor.moveToFirst();

        String id = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_DB_DEFAULT_ID));
        String firstName = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_LAST_NAME));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_USER_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_PASSWORD));
        String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_USER_PHOTO_URI));

        cursor.close();

        return new User(photoUri, email, firstName, lastName, id, username, password);
    }

    @NonNull
    public static ContentValues getProfileContentValues(User user, boolean profilePhotoChanged, String imageUri) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(PROFILE_COLUMN_FIRST_NAME, user.getFirstName());
        contentValues.put(PROFILE_COLUMN_LAST_NAME, user.getLastName());
        contentValues.put(PROFILE_COLUMN_USER_NAME, user.getUsername());
        contentValues.put(PROFILE_COLUMN_EMAIL, user.getEmail());
        contentValues.put(PROFILE_COLUMN_PASSWORD, user.getPassword());
        if (profilePhotoChanged){
            contentValues.put(PROFILE_COLUMN_USER_PHOTO_URI, imageUri);
        }

        return contentValues;
    }

    public static boolean validateUserInput(Context context, User user) {
        if(!isEmailValid(user.getEmail())){
            Toast.makeText(context, "Invalid Email",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if(user.getPassword().isEmpty() || user.getPassword().length() < 6){
            Toast.makeText(context, "Password as to be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        Cursor cursor = getUserDataFormDataBase(context, PROFILE_COLUMN_EMAIL, user.getEmail());

        if ((cursor.getCount() > 0)) {
            Toast.makeText(context, "Your account already exist, please login",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        cursor.close();
        return false;
    }

    public static boolean isEmailValid(String email) {
        final Pattern EMAIL_REGEX = Pattern
                .compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }
}
