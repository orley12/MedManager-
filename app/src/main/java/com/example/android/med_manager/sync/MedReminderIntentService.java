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
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.android.med_manager.MedWidgetProvider;
import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract.MedEntry;

import static com.example.android.med_manager.data.MedContract.MedEntry.CONTENT_URI;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MedReminderIntentService extends IntentService {

    public MedReminderIntentService() {
        super("MedReminderIntentService");
    }

    public static final String ACTION_UPDATE_MED_MANAGER_WIDGET = "update_med_manager_widget";

    public static void startActionUpdateMedWidgets(Context context) {
        Intent intent = new Intent(context, MedReminderIntentService.class);
        intent.setAction(ACTION_UPDATE_MED_MANAGER_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (ACTION_UPDATE_MED_MANAGER_WIDGET.equals(action)) {
            handleActionUpdatePlantWidgets();
        }else {
            Bundle bundle = intent.getExtras();
            long id = bundle.getLong("id");
            ReminderTasks.executeTask(this, action, id);

            cancelAlarm(this, id);
        }
    }

    private void handleActionUpdatePlantWidgets() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MedWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        //Now update all widgets
        MedWidgetProvider.updateMedWidget(this, appWidgetManager, appWidgetIds);

    }

    public void cancelAlarm(Context context, long id){

        Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(CONTENT_URI, id), null, null,
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