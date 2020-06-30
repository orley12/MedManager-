package com.example.android.med_manager.sync;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.med_manager.R;

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
 * Created by SOLARIN O. OLUBAYODE on 26/06/18.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        Cursor mCursor;

        public ListRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();
            mCursor = mContext.getContentResolver().query(
                    CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();        }

        @Override
        public RemoteViews getViewAt(int position) {

            mCursor.moveToPosition(position);
            long idIndex = mCursor.getLong(mCursor.getColumnIndexOrThrow(MED_DB_DEFAULT_ID));
            String name = mCursor.getString(mCursor.getColumnIndexOrThrow(MED_COLUMN_NAME));
            int type = mCursor.getInt(mCursor.getColumnIndexOrThrow(MED_COLUMN_TYPE));
            int dosage = mCursor.getInt(mCursor.getColumnIndexOrThrow(MED_COLUMN_DOSAGE));
            long startDate = mCursor.getLong(mCursor.getColumnIndexOrThrow(MED_COLUMN_START_DATE));
            long endDate = mCursor.getLong(mCursor.getColumnIndexOrThrow(MED_COLUMN_END_DATE));

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.med_widget_item);

            views.setTextViewText(R.id.med_name_widget, name);
            views.setTextViewText(R.id.med_dosage_widget, Integer.toString(dosage));
            views.setTextViewText(R.id.start_date_widget, reduceDateLength(convertFormMilliSecToDate(startDate)));
            views.setTextViewText(R.id.end_date_widget, reduceDateLength(convertFormMilliSecToDate(endDate)));
            views.setImageViewResource(R.id.med_type_image_widget, medTypeImage(type));

            // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
//            Bundle extras = new Bundle();
//            extras.putLong("id", idIndex);
            Uri currentMedDataUri = ContentUris.withAppendedId(CONTENT_URI, idIndex);
            Intent fillInIntent = new Intent();
            fillInIntent.setData(currentMedDataUri);
            views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
            return views;
        }

        private int medTypeImage(int type) {
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

        private String reduceDateLength(String dateValue){
            return dateValue.substring(0,dateValue.length()-5);
        }

        private String convertFormMilliSecToDate(long date) {
//        long dateValue = Long.parseLong(date);
            String dateFormat = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            return simpleDateFormat.format(calendar.getTime());
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    }
