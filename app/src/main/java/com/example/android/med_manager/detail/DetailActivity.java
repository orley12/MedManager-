package com.example.android.med_manager.detail;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.model.Medication;
import com.example.android.med_manager.utilities.Utils;

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
//        Log.i(TAG, "CONTAINER :" + mCurrentMedUri);

//        final long id = ContentUris.parseId(mCurrentMedUri);

        initViews();

        getSupportLoaderManager().initLoader(101, null, this);


    }

    private void initViews() {
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = getMedicationTableProjection();

        return new CursorLoader(
                DetailActivity.this,
                mCurrentMedUri,
                projection,
                null,
                null,
                null);
        }

    @NonNull
    private String[] getMedicationTableProjection() {
        return new String[]{
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
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    Medication med = getMedicationData(cursor);

    setViewProperties(med);
    }

    public Medication getMedicationData(Cursor cursor){

        cursor.moveToFirst();

        String medicationName = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
        int medicationType = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
        String medicationDescription = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DESCRIPTION));
        int medicationDosage = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
        int medicationInterval = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
        long medicationStartDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
        long medicationEndDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
        long medicationStartTime = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_TIME));
        int medicationTakenCount = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TAKEN_COUNT));
        int medicationIgnoreCount = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_IGNORE_COUNT));

        cursor.close();

        return new Medication(medicationName, medicationType, medicationDescription, medicationDosage,
                medicationInterval, medicationStartDate, medicationEndDate, medicationStartTime,
                medicationTakenCount, medicationIgnoreCount);
    }

    private void setViewProperties(Medication med) {
        mMedNameTextView.setText(med.getMedicationName());
        mMedTypeImageView.setImageResource((int) Utils.getPropertiesForMedTypeImageView(med.getMedicationType()).get("image"));
        mMedTypeImageView.setBackgroundColor((int) Utils.getPropertiesForMedTypeImageView(med.getMedicationType()).get("backgroundColor"));
        mMedTypeTextView.setText((int) Utils.getPropertiesForMedTypeImageView(med.getMedicationType()).get("backgroundColor"));
        mMedDescriptionTextView.setText(med.getMedicationDescription());
        mMedDosageTextView.setText(Integer.toString(med.getMedicationDosage()));
        mMedIntervalTextView.setText(Integer.toString(med.getMedicationInterval()));
        mMedStartDateTextView.setText(Utils.convertFormMilliSecToDate(med.getMedicationStartDate()));
        mMedEndDateTextView.setText(Utils.convertFormMilliSecToDate(med.getMedicationEndDate()));
        mMedNameSmallTextView.setText(med.getMedicationName());
        mTakenTextView.setText("You used your medications :" + med.getMedicationTakenCount() + " times.");
        mIgnoreTextView.setText("You ignored use of your medication :" + med.getMedicationIgnoreCount() + " times.");
        String startTimeString = Utils.convertFormMilliSecToTime(med.getMedicationStartTime());
        int startTimeInt = Integer.parseInt(startTimeString.substring(0, 2));
        if (startTimeInt >= 12) {
            mStartTimeTextView.setText(Utils.convertFormMilliSecToTime(med.getMedicationStartTime()) + " pm");
        } else {
            mStartTimeTextView.setText(Utils.convertFormMilliSecToTime(med.getMedicationStartTime()) + " am");
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
