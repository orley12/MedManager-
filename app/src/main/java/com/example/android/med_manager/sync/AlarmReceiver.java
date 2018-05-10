package com.example.android.med_manager.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.example.android.med_manager.data.MedContract.MedEntry;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
                // Set the alarm here.
                reRegisterAlarms(context);
                Log.d(TAG, "onReceive: MED_MANAGER_BOOT_COMPLETED");
    }

    public void reRegisterAlarms(Context context){
        String[] projection = {
                MedEntry.MED_DB_DEFAULT_ID,
        };
        Cursor mCursor = context.getContentResolver().query(MedEntry.CONTENT_URI,projection,
                null, null, null);
            while (mCursor.moveToNext()) {
                long idIndex = mCursor.getLong(mCursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
                NotificationScheduler.getId(context, idIndex);
            }
        mCursor.close();
    }
}