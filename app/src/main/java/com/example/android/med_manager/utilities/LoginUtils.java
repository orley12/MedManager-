package com.example.android.med_manager.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.CONTENT_URI;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.*;

public class LoginUtils {

    private static  String TAG = LoginUtils.class.getSimpleName();

    //checks if user who is logging in exists in the profile table
    public static Cursor getUserDataFormDataBase(Context context, String column, String value){
        String[] columnsToReturn = createProjection();
        String[] selectionArgs = {String.valueOf(value)};

        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI,
                columnsToReturn,
                column + "=?",
                selectionArgs,
                null
        );

        return cursor;
    }

    public static String checkIsNotNull(String value) {
        return  (value != null) ? value : "";
    }

    // simple utility function just to generate/ return a projection(columnsToReturn
    // form the profile table) this is just to make our code a bit neater
    private static String[] createProjection() {
        return new String[]{
                PROFILE_COLUMN_EMAIL,
                PROFILE_COLUMN_FIRST_NAME,
                PROFILE_COLUMN_LAST_NAME,
                PROFILE_COLUMN_USER_NAME,
                PROFILE_COLUMN_PASSWORD,
                PROFILE_ID_GOOGLE,
                PROFILE_DB_DEFAULT_ID,
                PROFILE_COLUMN_USER_PHOTO_URI

        };
    }

    public static boolean comparePasswords(String passwordInDatabase ,String inputtedPassword) {
        return inputtedPassword.equals(passwordInDatabase);
    }

//    public static boolean checkIfUserAsPassword(Context context, String passwordInDatabase) {
//
//        return false;
//    }

//
//    private static String[] allProfileColumnsProjection() {
//        String[] mProjection = {MedContract.ProfileEntry.PROFILE_COLUMN_EMAIL,
//
//    }

}