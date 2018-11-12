package com.example.android.med_manager.ui;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract.MedEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//import android.content.Loader;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = DetailActivity.class.getSimpleName() ;

    TextView mMedNameTextView;

    ImageView mMedTypeImageView;

    TextView mMedDescriptionTextView;

    TextView mMedDosageTextView;

    TextView mMedIntervalTextView;

    TextView mMedStartDateTextView;

    TextView mMedEndDateTextView;

    TextView mMedNameSmallTextView;

    TextView mMedTypeTextView;

    TextView mTakenTextView;

    TextView mIgnoreTextView;

    TextView mStartTimeTextView;

    Button mTakenButton;

    Button mIgnoreButton;

    Uri mCurrentMedUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mCurrentMedUri = intent.getData();
        Log.i(TAG, "CONTAINER :" + mCurrentMedUri);

//        final long id = ContentUris.parseId(mCurrentMedUri);

        mMedNameTextView = (TextView) findViewById(R.id.med_name_detail);
        mMedTypeImageView = (ImageView) findViewById(R.id.med_type_image_detail);
        mMedTypeTextView = (TextView) findViewById(R.id.med_type_text_detail);
        mMedDescriptionTextView = (TextView) findViewById(R.id.med_description_detail);
        mMedDosageTextView = (TextView) findViewById(R.id.med_dosage_detail);
        mMedIntervalTextView = (TextView) findViewById(R.id.med_interval_detail);
        mMedStartDateTextView = (TextView) findViewById(R.id.start_date_detail);
        mMedEndDateTextView = (TextView) findViewById(R.id.end_date_detail);
        mMedNameSmallTextView = (TextView) findViewById(R.id.med_name_small_detail);
        mTakenTextView = (TextView) findViewById(R.id.taken_count_text_view);
        mIgnoreTextView = (TextView) findViewById(R.id.ignore_count_text_view);
        mStartTimeTextView = (TextView) findViewById(R.id.start_time);

        getSupportLoaderManager().initLoader(101, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                MedEntry.MED_DB_DEFAULT_ID,
                MedEntry.MED_COLUMN_NAME,
                MedEntry.MED_COLUMN_TYPE,
                MedEntry.MED_COLUMN_DESCRIPTION,
                MedEntry.MED_COLUMN_DOSAGE,
                MedEntry.MED_COLUMN_INTERVAL,
                MedEntry.MED_COLUMN_START_TIME,
                MedEntry.MED_COLUMN_START_DATE,
                MedEntry.MED_COLUMN_END_DATE,
                MedEntry.MED_COLUMN_TAKEN_COUNT,
                MedEntry.MED_COLUMN_IGNORE_COUNT
        };
        return new CursorLoader(
                DetailActivity.this,
                mCurrentMedUri,
                projection,
                null,
                null,
                null);
        }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DESCRIPTION));
            int dosage = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
            int interval = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
            long startDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
            long endDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
            long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_TIME));
            int takenCount = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TAKEN_COUNT));
            int ignoreCount = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_IGNORE_COUNT));

            mMedNameTextView.setText(name);
            getImageForTypeImageView(type);
            getTextForTypeTextView(type);
            mMedDescriptionTextView.setText(description);
            mMedDosageTextView.setText(Integer.toString(dosage));
            mMedIntervalTextView.setText(Integer.toString(interval));
            mMedStartDateTextView.setText(convertFormMilliSecToDate(startDate));
            mMedEndDateTextView.setText(convertFormMilliSecToDate(endDate));
            mMedNameSmallTextView.setText(name);
            mTakenTextView.setText("You used your medications :" + takenCount + " times.");
            mIgnoreTextView.setText("You ignored use of your medication :" + ignoreCount + " times.");
            String startTimeString = convertFormMilliSecToTime(startTime);
            int startTimeInt = Integer.parseInt(startTimeString.substring(0, 2));
            if (startTimeInt >= 12) {
                mStartTimeTextView.setText(convertFormMilliSecToTime(startTime) + " pm");
            } else {
                mStartTimeTextView.setText(convertFormMilliSecToTime(startTime) + " am");
            }
        cursor.close();

    }

    private void getImageForTypeImageView(int type) {
        switch (type) {
            case MedEntry.MED_TYPE_CAPSULES:
                mMedTypeImageView.setImageResource(R.drawable.ic_capsule);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.capsule_color));
                break;
            case MedEntry.MED_TYPE_TABLETS:
                mMedTypeImageView.setImageResource(R.drawable.ic_tablet);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.tablet_color));
                break;
            case MedEntry.MED_TYPE_SYRUP:
                mMedTypeImageView.setImageResource(R.drawable.ic_syrup_liquid);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.syrup_color));
                break;
            case MedEntry.MED_TYPE_INHALER:
                mMedTypeImageView.setImageResource(R.drawable.ic_inhaler);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.inhaler_color));
                break;
            case MedEntry.MED_TYPE_DROPS:
                mMedTypeImageView.setImageResource(R.drawable.ic_eye_drop);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.eye_drop_color));
                break;
            case MedEntry.MED_TYPE_OINTMENT:
                mMedTypeImageView.setImageResource(R.drawable.ic_ointiment);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.onitement_color));
                break;
            case MedEntry.MED_TYPE_INJECTION:
                mMedTypeImageView.setImageResource(R.drawable.ic_injection);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.injection_color));
                break;
            case MedEntry.MED_TYPE_OTHERS:
                mMedTypeImageView.setImageResource(R.drawable.ic_other_meds);
                mMedTypeImageView.setBackgroundColor(getResources().getColor(R.color.others_color));
                break;
        }
    }

    private void getTextForTypeTextView(int type) {
        switch (type) {
            case MedEntry.MED_TYPE_CAPSULES:
                mMedTypeTextView.setText("Capusles");
                break;
            case MedEntry.MED_TYPE_TABLETS:
                mMedTypeTextView.setText("Tablets");
                break;
            case MedEntry.MED_TYPE_SYRUP:
                mMedTypeTextView.setText("Syrup");
                break;
            case MedEntry.MED_TYPE_INHALER:
                mMedTypeTextView.setText("Inhaler");
                break;
            case MedEntry.MED_TYPE_DROPS:
                mMedTypeTextView.setText("Drops");
                break;
            case MedEntry.MED_TYPE_OINTMENT:
                mMedTypeTextView.setText("Ointment");
                break;
            case MedEntry.MED_TYPE_INJECTION:
                mMedTypeTextView.setText("Injection");
                break;
            case MedEntry.MED_TYPE_OTHERS:
                mMedTypeTextView.setText("Other Medications");
                break;
        }
    }

    private String convertFormMilliSecToTime(long date) {
        long dateValue = date;
        String dateFormat = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        return simpleDateFormat.format(calendar.getTime());
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
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
