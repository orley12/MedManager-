package com.example.android.med_manager;

import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.sync.AlarmReceiver;
import com.example.android.med_manager.sync.NotificationScheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import com.example.android.med_manager.sync.NotificationScheduler;


public class MedFormActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MedFormActivity.class.getSimpleName();

    EditText mMedNameEditText;

    Spinner mMedTypeSpinner;

    EditText mMedDescriptionEditText;

    EditText mDosageEditText;

    EditText mIntervalEditText;

    EditText mStartDateEditText;

    EditText mEndDateEditText;

    EditText mStartTimeEditText;

    Switch mSwitchView;

    Uri mCurrentMedUri;
    MedListAdapter mMedListAdapter;
    private int mMedType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_med);

        Intent intent = getIntent();
        mCurrentMedUri = intent.getData();

//        mMedListAdapter = new MedListAdapter(this, listener, mListener);

        mMedNameEditText = findViewById(R.id.med_name);

        mMedTypeSpinner = findViewById(R.id.spinner_med_type);

        mMedDescriptionEditText = findViewById(R.id.med_description);

        mDosageEditText = findViewById(R.id.med_dosage);

        mIntervalEditText = findViewById(R.id.med_interval);

        mStartDateEditText = findViewById(R.id.med_start_date);

        mEndDateEditText = findViewById(R.id.med_end_date);

        mStartTimeEditText = findViewById(R.id.start_time_edit_text);

        mSwitchView = findViewById(R.id.switch_interval);

        setupSpinner();
        lunchTimePickerStartFragment();
        lunchDatePickerStartFragment();
        lunchDatePickerEndFragment();

        if (mCurrentMedUri == null) {
            setTitle("Add Medication");
        } else {
            setTitle("Edit Medication");
            getSupportLoaderManager().initLoader(102, null, this);
        }
    }

    private void setupSpinner() {
        ArrayAdapter medTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_med_types, android.R.layout.simple_spinner_item);

        medTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mMedTypeSpinner.setAdapter(medTypeSpinnerAdapter);

        mMedTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.med_type_capsules))) {
                        mMedType = MedEntry.MED_TYPE_CAPSULES;
                    } else if (selection.equals(getString(R.string.med_type_tablets))) {
                        mMedType = MedEntry.MED_TYPE_TABLETS;
                    } else if (selection.equals(getString(R.string.med_type_syrup))) {
                        mMedType = MedEntry.MED_TYPE_SYRUP;
                    } else if (selection.equals(getString(R.string.med_type_inhaler))) {
                        mMedType = MedEntry.MED_TYPE_INHALER;
                    } else if (selection.equals(getString(R.string.med_type_drops))) {
                        mMedType = MedEntry.MED_TYPE_DROPS;
                    } else if (selection.equals(getString(R.string.med_type_ointment))) {
                        mMedType = MedEntry.MED_TYPE_OINTMENT;
                    } else if (selection.equals(getString(R.string.med_type_injection))) {
                        mMedType = MedEntry.MED_TYPE_INJECTION;
                    } else if (selection.equals(getString(R.string.med_type_others))) {
                        mMedType = MedEntry.MED_TYPE_OTHERS;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMedType = MedEntry.MED_TYPE_OTHERS; // Unknown
            }
        });
    }

    public void lunchTimePickerStartFragment() {
        mStartTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newDatePickerStartFragment = new TimePickerFragment();
                newDatePickerStartFragment.show(getFragmentManager(), "timeStartPicker");
            }
        });
    }

    public void lunchDatePickerStartFragment() {
        mStartDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newDatePickerStartFragment = new DatePickerStartFragment();
                newDatePickerStartFragment.show(getFragmentManager(), "dateStartPicker");
            }
        });
    }

    public void lunchDatePickerEndFragment() {
        mEndDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newDatePickerEndFragment = new DatePickerEndFragment();
                newDatePickerEndFragment.show(getFragmentManager(), "dateEndPicker");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionSelectedId = item.getItemId();
        switch (optionSelectedId) {
            case R.id.action_save:
                if (mCurrentMedUri == null) {
                    insertMedDataIntoDb();
                    finish();
                } else {
                    updateMed();
                }
                finish();
                return true;
            case R.id.action_delete:
                deleteMedInfoFromDb();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertMedDataIntoDb() {
        String medName = mMedNameEditText.getText().toString().trim();
        String medDescription = mMedDescriptionEditText.getText().toString().trim();
        String medDosage = mDosageEditText.getText().toString().trim();
        String medInterval = mIntervalEditText.getText().toString().trim();
        String medStartTime = mStartTimeEditText.getText().toString().trim();
        if ( medStartTime.isEmpty()|| medStartTime.length() <= 0 ) {
            Toast.makeText(this, "Please Enter A Start Time", Toast.LENGTH_SHORT).show();
            return;
        }
        long medStartTimeMillSec = convertTimeToMillSec(medStartTime);
        String medStartDate = mStartDateEditText.getText().toString().trim();
        if ( medStartDate.isEmpty()|| medStartDate.length() <= 0 ) {
            Toast.makeText(this, "Please Enter A Start Date", Toast.LENGTH_SHORT).show();
            return;
        }
        long medStartMillSec = convertDateToMillSec(medStartDate);
        String medEndDate = mEndDateEditText.getText().toString().trim();
        if (medEndDate.isEmpty() || medEndDate.length() <= 0){
            Toast.makeText(this,"Please Enter An End Date",Toast.LENGTH_SHORT).show();
            return;
        }
        long medEndMillSec = convertDateToMillSec(medEndDate);
        int defaultForTakenIgnoreAndReminderCount = 0;

        Log.i(TAG, "TimeMillStartDateSec start:" + medStartMillSec + "   " + medEndMillSec + "  " + medStartTimeMillSec);


        ContentValues contentValues = new ContentValues();
        contentValues.put(MedEntry.MED_COLUMN_NAME, medName);
        contentValues.put(MedEntry.MED_COLUMN_TYPE, mMedType);
        contentValues.put(MedEntry.MED_COLUMN_DESCRIPTION, medDescription);
        contentValues.put(MedEntry.MED_COLUMN_DOSAGE, medDosage);
        contentValues.put(MedEntry.MED_COLUMN_INTERVAL, medInterval);
        contentValues.put(MedEntry.MED_COLUMN_START_DATE, medStartMillSec);
        contentValues.put(MedEntry.MED_COLUMN_END_DATE, medEndMillSec);
        contentValues.put(MedEntry.MED_COLUMN_START_TIME,medStartTimeMillSec);
        contentValues.put(MedEntry.MED_COLUMN_TAKEN_COUNT, defaultForTakenIgnoreAndReminderCount);
        contentValues.put(MedEntry.MED_COLUMN_IGNORE_COUNT, defaultForTakenIgnoreAndReminderCount);
        contentValues.put(MedEntry.MED_COLUMN_REMINDER_COUNT, defaultForTakenIgnoreAndReminderCount);


        Uri returnedUri = getContentResolver().insert(MedEntry.CONTENT_URI, contentValues);
        if (returnedUri != null) {
            Toast.makeText(this, "Medication info as been saved" + returnedUri, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unable to save Medication Info", Toast.LENGTH_SHORT).show();
        }
        long idFromReturnedUri = ContentUris.parseId(returnedUri);
        Log.i(TAG, "PASRED URI :" + idFromReturnedUri);
//        ReminderUtilities.getId(MedFormActivity.this, idFromReturnedUri);
        NotificationScheduler.getId(MedFormActivity.this, idFromReturnedUri);
    }

    public long convertTimeToMillSec(String medInterval) {
        String extractDateSubString = medInterval.substring(0, medInterval.length() - 3);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String dateString = extractDateSubString;
        Date dateObject = null;
        try {
            dateObject = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObject);
        long intervalTimeMill = calendar.getTimeInMillis();
        Log.i(TAG, "TimeMillSec:" + intervalTimeMill);
        return intervalTimeMill;
    }

    public long convertDateToMillSec(String medStartDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = medStartDate;
        Date dateObject = null;
        try {
            dateObject = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObject);
        long intervalTimeMill = calendar.getTimeInMillis();
        return intervalTimeMill;
    }

    private void updateMed() {
        String medName = mMedNameEditText.getText().toString().trim();
        String medDescription = mMedDescriptionEditText.getText().toString().trim();
        String medDosage = mDosageEditText.getText().toString().trim();
        String medInterval = mIntervalEditText.getText().toString().trim();
        String medStartTime = mStartTimeEditText.getText().toString().trim();
        if ( medStartTime.isEmpty()|| medStartTime.length() <= 0 ) {
            Toast.makeText(this, "Please Enter A Start Time", Toast.LENGTH_SHORT).show();
            return;
        }
        long medStartTimeMillSec = convertTimeToMillSec(medStartTime);

        String medStartDate = mStartDateEditText.getText().toString().trim();
        if ( medStartDate.isEmpty()|| medStartDate.length() <= 0 ){
            Toast.makeText(this,"Please Enter A Start Date",Toast.LENGTH_SHORT).show();
            return;
        }
        long medStartMillSec = convertDateToMillSec(medStartDate);

        String medEndDate = mEndDateEditText.getText().toString().trim();
        if (medEndDate.isEmpty() || medEndDate.length() <= 0){
            Toast.makeText(this,"Please Enter An End Date",Toast.LENGTH_SHORT).show();
            return;
        }
        long medEndMillSec = convertDateToMillSec(medEndDate);


        ContentValues contentValues = new ContentValues();
        contentValues.put(MedEntry.MED_COLUMN_NAME, medName);
        contentValues.put(MedEntry.MED_COLUMN_TYPE, mMedType);
        contentValues.put(MedEntry.MED_COLUMN_DESCRIPTION, medDescription);
        contentValues.put(MedEntry.MED_COLUMN_DOSAGE, Integer.parseInt(medDosage));
        contentValues.put(MedEntry.MED_COLUMN_START_TIME,medStartTimeMillSec);
        contentValues.put(MedEntry.MED_COLUMN_INTERVAL, medInterval);
        contentValues.put(MedEntry.MED_COLUMN_START_DATE, medStartMillSec);
        contentValues.put(MedEntry.MED_COLUMN_END_DATE, medEndMillSec);

        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentMedUri))};
        int newUri = getContentResolver().update(MedEntry.CONTENT_URI, contentValues, selection, selectionArgs);

        if (newUri > 0) {
            Toast.makeText(this, "Pets Update Successful"
                    + ContentUris.parseId(mCurrentMedUri), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pets Update Failed", Toast.LENGTH_SHORT).show();
        }
        long idValueOfParseUri = ContentUris.parseId(mCurrentMedUri);
                    NotificationScheduler.cancelReminder(MedFormActivity.this, AlarmReceiver.class,idValueOfParseUri);
                    NotificationScheduler.getId(MedFormActivity.this,idValueOfParseUri);
    }

    private void deleteMedInfoFromDb() {
        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentMedUri))};
        int newUri = getContentResolver().delete(MedEntry.CONTENT_URI, selection, selectionArgs);

        if (newUri > 0) {
            Toast.makeText(this, "Medication Deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Medication Could Not Deleted", Toast.LENGTH_SHORT).show();
        }
        long idValueOfParseUri = ContentUris.parseId(mCurrentMedUri);
        NotificationScheduler.cancelReminder(MedFormActivity.this, AlarmReceiver.class,idValueOfParseUri);
        getSupportLoaderManager().restartLoader(102, null, this);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MedEntry.MED_DB_DEFAULT_ID,
                MedEntry.MED_COLUMN_NAME,
                MedEntry.MED_COLUMN_TYPE,
                MedEntry.MED_COLUMN_DESCRIPTION,
                MedEntry.MED_COLUMN_DOSAGE,
                MedEntry.MED_COLUMN_INTERVAL,
                MedEntry.MED_COLUMN_START_TIME,
                MedEntry.MED_COLUMN_START_DATE,
                MedEntry.MED_COLUMN_END_DATE
        };
        return new CursorLoader(
                this,
                mCurrentMedUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        if (mCursor.moveToFirst()) {
            String name = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
            int type = mCursor.getInt(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
            String description = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DESCRIPTION));
            int dosage = mCursor.getInt(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
            String interval = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
            String startDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
            String endDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
            long startTime = mCursor.getLong(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_TIME));

            Log.i(TAG, "onLoadFinished:  HERE!!!" + name + type + description);
            mMedNameEditText.setText(name);
            mMedTypeSpinner.setSelection(type);
            mMedDescriptionEditText.setText(description);
            mDosageEditText.setText(Integer.toString(dosage));
            mIntervalEditText.setText(interval);
            mStartDateEditText.setText(convertFormMilliSecToDate(startDate));
            mEndDateEditText.setText(convertFormMilliSecToDate(endDate));
            String startTimeString = convertFormMilliSecToTime(startTime);
            int startTimeInt = Integer.parseInt(startTimeString.substring(0,2));
            if (startTimeInt >= 12) {
                mStartTimeEditText.setText(convertFormMilliSecToTime(startTime) + " pm");
            }else {
                mStartTimeEditText.setText(convertFormMilliSecToTime(startTime) + " am");
            }

        }
        mCursor.close();
    }

    private String convertFormMilliSecToTime(long date) {
        long dateValue = date;
        String dateFormat = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        return simpleDateFormat.format(calendar.getTime());
    }

    private String convertFormMilliSecToDate(String date) {
        long dateValue = Long.parseLong(date);
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        return simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}