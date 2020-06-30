package com.example.android.med_manager.profile;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.CONTENT_URI;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_DB_DEFAULT_ID;

public class ProfileRepository {

    public static void updateProfile(Context context, ContentValues contentValues, long id) {

        String selection = PROFILE_DB_DEFAULT_ID + "=?" ;

        String[] selectionArgs = {String.valueOf(id)};

        context.getContentResolver().update(CONTENT_URI, contentValues, selection, selectionArgs);
    }

    public static void insertProfile(Context context, ContentValues contentValues) {
        Uri returnedUri = context.getContentResolver().insert(CONTENT_URI,contentValues);
//        return ContentUris.parseId(returnedUri);
    }

}
