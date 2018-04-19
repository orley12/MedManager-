package com.example.android.med_manager.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.android.med_manager.data.MedContract.MedEntry;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Bundle bundle = intent.getExtras();
        long id = bundle.getLong("id");

        cancelAlarm(context,id);
        Intent intent1 = new Intent(context, MedReminderIntentService.class);
        intent1.setAction(ReminderTasks.ACTION_TAKE_MED_REMINDER);
        intent1.putExtra("id", id);
        context.startService(intent1);

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                return;
            }
        }
    }

    public void cancelAlarm(Context context, long id){
        String[] projection = new String[]{
                MedEntry.MED_COLUMN_END_DATE,
        };
        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        Cursor cursor = context.getContentResolver().query(MedEntry.CONTENT_URI, projection, selection,
                selectionArgs, null);
        long endDate = 0;
        if (cursor.moveToFirst()) {
            endDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
        }
        if (NotificationScheduler.todaysDate(endDate) == true){
            NotificationScheduler.cancelReminder(context,AlarmReceiver.class,id);
        }
    }
}