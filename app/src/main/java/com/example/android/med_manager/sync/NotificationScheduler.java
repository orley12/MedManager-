package com.example.android.med_manager.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.example.android.med_manager.data.MedContract.MedEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Jaison on 20/06/17.
 */

public class NotificationScheduler
{
    public static int DAILY_REMINDER_REQUEST_CODE;
    public static final String TAG="NotificationScheduler";

    public static void setReminder(Context context, int hour, int min, long id, int interval, int hourOrMinute) {
        long intervalTime = 0;
        long minuteMilli = (1000 * 60);
        if (hourOrMinute == 1) {
            intervalTime = minuteMilli * interval;
        }   else {
            long hourMilli = (minuteMilli * 60);
            intervalTime = hourMilli * interval;
        }
        Log.i(TAG,"HOURVALUE : " + intervalTime);

        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        Intent intent1 = new Intent(context, MedReminderIntentService.class);
        intent1.putExtra("id",id);
        intent1.setAction(ReminderTasks.ACTION_TAKE_MED_REMINDER);
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Log.i(TAG, "setReminder: " + id);
        am.setRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis() , intervalTime, pendingIntent);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        int status = context.getPackageManager().getComponentEnabledSetting(receiver);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            context.getPackageManager().setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
        }else {
            Log.i(TAG,"Receiver Disabled");
        }
    }

    public static void cancelReminder(Context context, long id) {
        // Disable a receiver
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(context, MedReminderIntentService.class);
        intent1.putExtra("id",id);
        intent1.setAction(ReminderTasks.ACTION_TAKE_MED_REMINDER);
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i(TAG, "cancelReminder: " + id);
            am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void reRegisterAlarms(Context context){
        String[] projection = {
                MedEntry.MED_DB_DEFAULT_ID,
        };
        Cursor mCursor = context.getContentResolver().query(MedEntry.CONTENT_URI,projection,
                null, null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
        while (mCursor.moveToNext()) {
                long idIndex = mCursor.getLong(mCursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
                NotificationScheduler.getId(context, idIndex);
            }
        }
        assert mCursor != null;
        mCursor.close();
    }

    public static void unRegisterAlarms(Context context){
        String[] projection = {
                MedEntry.MED_DB_DEFAULT_ID,
        };
        Cursor mCursor = context.getContentResolver().query(MedEntry.CONTENT_URI,projection,
                null, null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
            while (mCursor.moveToNext()) {
                long idIndex = mCursor.getLong(mCursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
                NotificationScheduler.cancelReminder(context, idIndex);
            }
        }
        assert mCursor != null;
        mCursor.close();
    }

    public static void getId(Context context, long idFromReturnedUri) {

        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(
                MedEntry.CONTENT_URI, idFromReturnedUri), null, null,
                null, null);
        long startTimeMill = 0;
        int interval = 0;
        int hourOrMinute;

        cursor.moveToFirst();
            startTimeMill = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_TIME));
            interval = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
            hourOrMinute = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_HOURS_OR_MINUTES));


        cursor.close();

        String startTime = convertFormMilliSecToTime(startTimeMill);
        String hours = startTime.substring(0,2);
        String min = startTime.substring(3,startTime.length());
        int hoursInt = Integer.parseInt(hours);
        int minInt = Integer.parseInt(min);
        NotificationScheduler.setReminder(context,hoursInt,minInt,idFromReturnedUri,interval,hourOrMinute);
    }

    public static boolean todaysDate(long endDate) {
        String endDateValue = convertFormMilliSecToDate(endDate);
        int day = Integer.parseInt(endDateValue.substring(0,2));
        int month = Integer.parseInt(endDateValue.substring(3,5));
        int year = Integer.parseInt(endDateValue.substring(6,endDateValue.length()));

        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.DAY_OF_MONTH, day);
        setcalendar.set(Calendar.MONTH, month);
        setcalendar.set(Calendar.YEAR, year);

                if(setcalendar.before(calendar)){
                    return true;
                }else {
                    return false;
                }
    }

    public static String convertFormMilliSecToDate(long date) {
        long dateValue = date;
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        return simpleDateFormat.format(calendar.getTime());
    }

    private static String convertFormMilliSecToTime(long startTimeMill) {
        long dateValue = startTimeMill;
        String dateFormat = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        return simpleDateFormat.format(calendar.getTime());
    }
}
