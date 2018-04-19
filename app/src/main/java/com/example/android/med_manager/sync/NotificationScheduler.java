package com.example.android.med_manager.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
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

    public static void setReminder(Context context, Class<?> cls, int hour, int min, long id, int interval) {
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
//        cancelReminder(context,cls,id);

//        if(setcalendar.before(calendar))
//            setcalendar.add(Calendar.DATE,1);

        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        ;
        Log.i("NOTIFICATION :","" + id);

        Intent intent1 = new Intent(context, cls);
        intent1.putExtra("id",id);
        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY/interval, pendingIntent);
    }

    public static void cancelReminder(Context context, Class<?> cls, long id) {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void getId(Context context, long idFromReturnedUri) {
                String[] projection = {
                MedEntry.MED_COLUMN_START_TIME,
                MedEntry.MED_COLUMN_INTERVAL
        };
        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(idFromReturnedUri)};
        Cursor cursor = context.getContentResolver().query(MedEntry.CONTENT_URI, projection, selection,
                selectionArgs, null);
        long startTimeMill = 0;
        int interval = 0;
        if (cursor.moveToFirst()) {
            startTimeMill = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_TIME));
            interval = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
        }
        cursor.close();

        String startTime = convertFormMilliSecToTime(startTimeMill);
        String hours = startTime.substring(0,2);
        String min = startTime.substring(3,startTime.length());
        int hoursInt = Integer.parseInt(hours);
        int minInt = Integer.parseInt(min);
        Log.i(TAG,"INTERVAL : " + hoursInt + " " + minInt);
        NotificationScheduler.setReminder(context, AlarmReceiver.class,hoursInt,minInt,idFromReturnedUri,interval);
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
