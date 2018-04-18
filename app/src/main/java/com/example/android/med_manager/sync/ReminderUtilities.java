///*
// * Copyright (C) 2016 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.example.android.med_manager.sync;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.example.android.med_manager.data.MedContract.MedEntry;
//import com.firebase.jobdispatcher.Constraint;
//import com.firebase.jobdispatcher.Driver;
//import com.firebase.jobdispatcher.FirebaseJobDispatcher;
//import com.firebase.jobdispatcher.GooglePlayDriver;
//import com.firebase.jobdispatcher.Job;
//import com.firebase.jobdispatcher.Lifetime;
//import com.firebase.jobdispatcher.Trigger;
//
//import java.util.concurrent.TimeUnit;
//
//public class ReminderUtilities {
//    private static final String LOG_TAG = ReminderUtilities.class.getSimpleName();
//    /*
//     * Interval at which to remind the user to drink water. Use TimeUnit for convenience, rather
//     * than writing out a bunch of multiplication ourselves and risk making a silly mistake.
//     */
////    private static long REMINDER_INTERVAL_MINUTES = 0;
////    private static int REMINDER_INTERVAL_SECONDS = 0;
////    private static  int SYNC_FLEXTIME_SECONDS = 0;
//
//    static Bundle sBundle;
//
//    private static String REMINDER_JOB_TAG = "";
//
//    private static boolean sInitialized;
//
//    public static final void getId(Context context, long id) {
//        String[] projection = {
//                MedEntry.MED_COLUMN_INTERVAL,
//                MedEntry.MED_COLUMN_END_DATE,
//                MedEntry.MED_COLUMN_START_DATE
//        };
//        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
//        String[] selectionArgs = new String[]{Long.toString(id)};
//        Cursor cursor = context.getContentResolver().query(MedEntry.CONTENT_URI, projection, selection,
//                selectionArgs, null);
//        int interval = 0;
//        if (cursor.moveToFirst()) {
//            interval = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
//            long endDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
//            long startDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
//        }
//
////        REMINDER_JOB_TAG =
//
////        convertIntervalToMinites(interval);
//        cursor.close();
////        sBundle = new Bundle ();
////        sBundle.putLong("idForFireBaseJob", id);
//        scheduleMedNotificationReminder(context, id, interval);
//    }
//
////    private static void convertIntervalToMinites(int interval) {
////        REMINDER_INTERVAL_MINUTES = interval/*(int) ((timeInterval / 1000) / 60)*/;
////        REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
////        SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;
////    }
////
////    private static void convertIntervalToHours(int interval) {
////        REMINDER_INTERVAL_MINUTES = interval/*(int) ((timeInterval / 1000) / 60)*/;
////        REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(REMINDER_INTERVAL_MINUTES));
////        SYNC_FLEXTIME_SECONDS= REMINDER_INTERVAL_SECONDS;
////    }
//
//    synchronized public static void scheduleMedNotificationReminder(@NonNull final Context context, long id, int interval) {
//        Bundle bundle = new Bundle();
//        bundle.putLong("idForFireBaseJob", id);
//
//        String jobTag = Long.toString(id);
//
//        long reminderIntervalMinutes = interval/*(int) ((timeInterval / 1000) / 60)*/;
//        int reminderIntervalSeconds = (int) (TimeUnit.MINUTES.toSeconds(reminderIntervalMinutes));
//        int syncFlextimeSeconds = reminderIntervalSeconds;
//
//        Log.i(LOG_TAG, "JOB_TAG " + jobTag + "   " + reminderIntervalSeconds);
//
//
//        if (sInitialized) return;
//
//        Driver mDriver = new GooglePlayDriver(context);
//        FirebaseJobDispatcher mDispatcher = new FirebaseJobDispatcher(mDriver);
//        /* Create the Job to periodically create reminders to drink water */
//        Job constraintReminderJob = mDispatcher.newJobBuilder()
//                /* The Service that will be used to write to preferences */
//                .setService(MedReminderFirebaseJobService.class)
//                /*
//                 * Set the UNIQUE tag used to identify this Job.
//                 */
//                .setTag(jobTag)
//                /*
//                 * Network constraints on which this Job should run. In this app, we're using the
//                 * device charging constraint so that the job only executes if the device is
//                 * charging.
//                 *
//                 * In a normal app, it might be a good idea to include a preference for this,
//                 * as different users may have different preferences on when you should be
//                 * syncing your application's data.
//                 */
//                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING,
//                        Constraint.ON_UNMETERED_NETWORK)
//                /*
//                 * setLifetime sets how long this job should persist. The options are to keep the
//                 * Job "forever" or to have it die the next time the device boots up.
//                 */
//                .setLifetime(Lifetime.FOREVER)
//                /*
//                 * We want these reminders to continuously happen, so we tell this Job to recur.
//                 */
//                .setRecurring(true)
//                .setExtras(bundle)
//                /*
//                 * We want the reminders to happen every 15 minutes or so. The first argument for
//                 * Trigger class's static executionWindow method is the start of the time frame
//                 * when the
//                 * job should be performed. The second argument is the latest point in time at
//                 * which the data should be synced. Please note that this end time is not
//                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
//                 */
//                .setTrigger(Trigger.executionWindow(
//                        reminderIntervalSeconds,
//                        reminderIntervalSeconds + syncFlextimeSeconds))
//                /*
//                 * If a Job with the tag with provided already exists, this new job will replace
//                 * the old one.
//                 */
//                .setReplaceCurrent(true)
//                /* Once the Job is ready, call the builder's build method to return the Job */
//                .build();
//
//        /* Schedule the Job with the dispatcher */
//        mDispatcher.schedule(constraintReminderJob);
//
//        /* The job has been initialized */
//        sInitialized = true;
//    }
//
//    public static final void cancelSpecifiedJob(Context context, long id) {
//        String specifiedJobTag = Long.toString(id);
//        Log.i(LOG_TAG, "HEYOOOO!!!" + specifiedJobTag);
////        mDispatcher.cancel(specifiedJobTag);
//    }
//}
