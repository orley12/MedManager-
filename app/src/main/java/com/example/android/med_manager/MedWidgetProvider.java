package com.example.android.med_manager;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

import com.example.android.med_manager.sync.ListWidgetService;
import com.example.android.med_manager.sync.MedReminderIntentService;
import com.example.android.med_manager.sync.ReminderTasks;
import com.example.android.med_manager.detail.DetailActivity;
import com.example.android.med_manager.ui.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.android.med_manager.data.MedContract.MedEntry.CONTENT_URI;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_COLUMN_DOSAGE;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_COLUMN_END_DATE;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_COLUMN_NAME;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_COLUMN_START_DATE;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_COLUMN_TYPE;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_DB_DEFAULT_ID;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_CAPSULES;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_DROPS;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_INHALER;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_INJECTION;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_OINTMENT;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_OTHERS;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_SYRUP;
import static com.example.android.med_manager.data.MedContract.MedEntry.MED_TYPE_TABLETS;

/**
 * Implementation of App Widget functionality.
 */
public class MedWidgetProvider extends AppWidgetProvider {

    private static final long INVALID_MED_ID = -1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
      MedReminderIntentService.startActionUpdateMedWidgets(context);
    }

    public static void updateMedWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        RemoteViews rv;
        if (height < 300) {
            rv = getSingleMedRemoteView(context);
        } else {
            rv = getMedListRemoteView(context);
        }
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private static RemoteViews getSingleMedRemoteView(Context context) {

        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI,
                null,
                null,
                null,
                null
        );
        long medId = INVALID_MED_ID;

        cursor.moveToFirst();
        medId = cursor.getLong(cursor.getColumnIndexOrThrow(MED_DB_DEFAULT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MED_COLUMN_NAME));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(MED_COLUMN_TYPE));
        int dosage = cursor.getInt(cursor.getColumnIndexOrThrow(MED_COLUMN_DOSAGE));
        long startDate = cursor.getLong(cursor.getColumnIndexOrThrow(MED_COLUMN_START_DATE));
        long endDate = cursor.getLong(cursor.getColumnIndexOrThrow(MED_COLUMN_END_DATE));
        cursor.close();

        Intent intent;
        if (medId == INVALID_MED_ID) {
            intent = new Intent(context, HomeActivity.class);
        } else { // Set on click to open the corresponding detail activity
            intent = new Intent(context, DetailActivity.class);
            Uri medDataUri = ContentUris.withAppendedId(CONTENT_URI, medId);
            intent.setData(medDataUri);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.med_widget_item);
        // Update image and text
        views.setTextViewText(R.id.med_name_widget, name);
        views.setTextViewText(R.id.med_dosage_widget, Integer.toString(dosage));
        views.setTextViewText(R.id.start_date_widget, reduceDateLength(convertFormMilliSecToDate(startDate)));
        views.setTextViewText(R.id.end_date_widget, reduceDateLength(convertFormMilliSecToDate(endDate)));
        views.setImageViewResource(R.id.med_type_image_widget, medTypeImage(type));

        views.setOnClickPendingIntent(R.id.widget_item, pendingIntent);

        Intent incrementTakenCountIntent = new Intent(context, MedReminderIntentService.class);
        incrementTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_TAKEN_COUNT);
        incrementTakenCountIntent.putExtra("id", medId);

        PendingIntent takePendingIntent = PendingIntent.getService(context, 0, incrementTakenCountIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.taken_layout_widget, takePendingIntent);

        Intent incrementIgnoreCountIntent = new Intent(context, MedReminderIntentService.class);
        incrementIgnoreCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_IGNORE_COUNT);
        incrementIgnoreCountIntent.putExtra("id", medId);

        PendingIntent ignorePendingIntent = PendingIntent.getService(context, 0, incrementIgnoreCountIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.ignore_layout_widget, ignorePendingIntent);

        return views;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static RemoteViews getMedListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        Intent appIntent = new Intent(context, DetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

        return views;
    }

    private static int medTypeImage(int type) {
        int medImage = 0;
        switch (type) {
            case MED_TYPE_CAPSULES:
                medImage = R.drawable.ic_capsule;
                break;
            case MED_TYPE_TABLETS:
                medImage = R.drawable.ic_tablet;
                break;
            case MED_TYPE_SYRUP:
                medImage = R.drawable.ic_syrup_liquid;
                break;
            case MED_TYPE_INHALER:
                medImage = R.drawable.ic_inhaler;
                break;
            case MED_TYPE_DROPS:
                medImage = R.drawable.ic_drops;
                break;
            case MED_TYPE_OINTMENT:
                medImage = R.drawable.ic_ointment;
                break;
            case MED_TYPE_INJECTION:
                medImage = R.drawable.ic_injection;
                break;
            case MED_TYPE_OTHERS:
                medImage = R.drawable.ic_other_meds;
                break;
        }
        return medImage;
    }

    private static String reduceDateLength(String dateValue){
        return dateValue.substring(0,dateValue.length()-5);
    }

    private static String convertFormMilliSecToDate(long date) {
//        long dateValue = Long.parseLong(date);
        String dateFormat = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        MedReminderIntentService.startActionUpdateMedWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}

