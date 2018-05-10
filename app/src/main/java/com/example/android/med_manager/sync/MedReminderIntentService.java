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

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.android.med_manager.data.MedContract.MedEntry;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MedReminderIntentService extends IntentService {

    public MedReminderIntentService() {
        super("MedReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        long id = bundle.getLong("id");
        ReminderTasks.executeTask(this, action, id);
        cancelAlarm(this,id);
    }

    public void cancelAlarm(Context context, long id){

        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(MedEntry.CONTENT_URI, id), null, null,
                null, null);
        long endDate = 0;
        cursor.moveToFirst();
            endDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));

            cursor.close();

        if (NotificationScheduler.todaysDate(endDate) == true){
            NotificationScheduler.cancelReminder(context,id);
        }
    }
}