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
package com.example.android.med_manager.sync;

import android.content.Context;

import com.example.android.med_manager.utilities.CountUtils;
import com.example.android.med_manager.utilities.NotificationUtils;

public class ReminderTasks {

    public static final String ACTION_INCREMENT_MED_TAKEN_COUNT = "increment-med-taken-count";
    public static final String ACTION_INCREMENT_MED_IGNORE_COUNT = "increment-med-ignore-count";
    public static final String ACTION_TAKE_MED_REMINDER = "take-med-reminder";

    private static final String TAG = ReminderTasks.class.getSimpleName();

    public static void executeTask(Context context, String action, long id) {
        if (ACTION_INCREMENT_MED_TAKEN_COUNT.equals(action)) {
            incrementMedTakenCount(context, id);
        } else if (ACTION_INCREMENT_MED_IGNORE_COUNT.equals(action)) {
            incrementMedIgnoreCount(context, id);
        } else if (ACTION_TAKE_MED_REMINDER.equals(action)) {
            issueMedReminder(context, id);
        }
    }

    private static void incrementMedTakenCount(Context context, long id) {
        CountUtils.incrementTakenCount(context, id);
        NotificationUtils.clearAllNotifications(context);
    }

    private static void incrementMedIgnoreCount(Context context, long id) {
        CountUtils.incrementIgnoreCount(context, id);
        NotificationUtils.clearAllNotifications(context);
    }

    private static void issueMedReminder(Context context, long id) {
            NotificationUtils.remindUserToTakeMed(context, id);
    }
}