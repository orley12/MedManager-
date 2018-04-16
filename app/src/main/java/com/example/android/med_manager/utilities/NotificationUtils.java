/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.med_manager.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.content.ContextCompat;

import com.example.android.med_manager.HomeActivity;
import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.sync.MedReminderIntentService;
import com.example.android.med_manager.sync.ReminderTasks;


/**
 * Utility class for creating hydration notifications
 */
public class NotificationUtils {

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1001 is in no way significant.
     */
    private static final int MED_REMINDER_NOTIFICATION_ID = 1001;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     * This pending intent will be used to lunch the HomeActivity from the notification
     */
    private static final int MED_REMINDER_PENDING_INTENT_ID = 3003;

    /*This pending intent will be used to increment the taken count*/
    private static final int ACTION_TAKEN_PENDING_INTENT_ID = 101;

    /*This pending intent will be used to increment the ignore count*/
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 104;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String MED_REMINDER_NOTIFICATION_CHANNEL_ID = "med_reminder_notification_channel";


    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserToTakeMed(Context context, long id) {

        String[] projection = {
                MedEntry.MED_COLUMN_NAME,
                MedEntry.MED_COLUMN_DOSAGE
        };
        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        Cursor cursor = context.getContentResolver().query(MedEntry.CONTENT_URI, projection, selection,
                selectionArgs, null);
        String medName = null;
        String medDosage = null;
        if (cursor.moveToFirst()) {
            medName = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
            medDosage = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
        }
//        cursor.close();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MED_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MED_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_small_capsule_for_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.med_reminder_notification_title))
                .setContentText(context.getString(R.string.med_reminder_notification_body) + medName
                        + context.getString(R.string.dosage_body) + medDosage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.med_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(drinkWaterAction(context, id))
                .addAction(ignoreReminderAction(context, id))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(MED_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static Action ignoreReminderAction(Context context, long id) {
        Intent ignoreReminderIntent = new Intent(context, MedReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_IGNORE_COUNT);
        ignoreReminderIntent.putExtra("id", id);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Action ignoreReminderAction = new Action(R.drawable.ic_ignore_ii,
                "No, thanks.",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static Action drinkWaterAction(Context context, long id) {
        Intent incrementMedTakenCountIntent = new Intent(context, MedReminderIntentService.class);
        incrementMedTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_TAKEN_COUNT);
        incrementMedTakenCountIntent.putExtra("id", id);
        PendingIntent incrementMedTakenPendingIntent = PendingIntent.getService(
                context,
                ACTION_TAKEN_PENDING_INTENT_ID,
                incrementMedTakenCountIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Action drinkWaterAction = new Action(R.drawable.ic_small_capsule_for_card_i,
                "I did it!",
                incrementMedTakenPendingIntent);
        return drinkWaterAction;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, HomeActivity.class);
        return PendingIntent.getActivity(
                context,
                MED_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_small_capsule_for_card_i);
        return largeIcon;
    }
}