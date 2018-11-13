package com.example.android.med_manager.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by SOLARIN O. OLUBAYODE on 30/07/18.
 */

public class PreferenceUtils {

    private static String KEY_ACCT_ID = "key_account_id";

    synchronized public static void setLoggedInUser(Context context, long id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_ACCT_ID, id);
        editor.apply();
    }

    synchronized public static long  getLoggedInUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(KEY_ACCT_ID, 0);
    }
    synchronized public static void setLoggedOut(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCT_ID);
        editor.apply();
    }

}
