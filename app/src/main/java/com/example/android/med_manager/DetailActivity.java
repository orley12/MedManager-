package com.example.android.med_manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.utilities.CountUtilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        String name = bundle.getString("name");
        int type = bundle.getInt("type");
        String description = bundle.getString("description");
        int dosage = bundle.getInt("dosage");
        String interval = bundle.getString("interval");
        String startDate = bundle.getString("startDate");
        String endDate = bundle.getString("endDate");
        int takenCount = bundle.getInt("takenCount");
        int ignoreCount = bundle.getInt("ignoreCount");
        long startTime = bundle.getLong("startTime");
        String id = bundle.getString("id");

        Log.i(TAG,"STARTTIME :" + id);


        mMedNameTextView = findViewById(R.id.med_name_detail);
        mMedNameTextView.setText(name);
        mMedTypeImageView = findViewById(R.id.med_type_image_detail);
        getImageForTypeImageView(type);
        mMedTypeTextView = findViewById(R.id.med_type_text_detail);
        getTextForTypeTextView(type);
        mMedDescriptionTextView = findViewById(R.id.med_description_detail);
        mMedDescriptionTextView.setText(description);
        mMedDosageTextView = findViewById(R.id.med_dosage_detail);
        mMedDosageTextView.setText(Integer.toString(dosage));
        mMedIntervalTextView = findViewById(R.id.med_interval_detail);
        mMedIntervalTextView.setText(interval);
        mMedStartDateTextView = findViewById(R.id.start_date_detail);
        mMedStartDateTextView.setText(convertFormMilliSecToDate(startDate));
        mMedEndDateTextView = findViewById(R.id.end_date_detail);
        mMedEndDateTextView.setText(convertFormMilliSecToDate(endDate));
        mMedNameSmallTextView = findViewById(R.id.med_name_small_detail);
        mMedNameSmallTextView.setText(name);
        mTakenTextView = findViewById(R.id.taken_count_text_view);
        mTakenTextView.setText("You used your medications :" + takenCount + " times.");
        mIgnoreTextView = findViewById(R.id.ignore_count_text_view);
        mIgnoreTextView.setText("You ignored use of your medication :" + ignoreCount + " times.");
        mStartTimeTextView = findViewById(R.id.start_time);
        String startTimeString = convertFormMilliSecToTime(startTime);
        int startTimeInt = Integer.parseInt(startTimeString.substring(0,2));
        if (startTimeInt >= 12) {
            mStartTimeTextView.setText(convertFormMilliSecToTime(startTime) + " pm");
        }else {
            mStartTimeTextView.setText(convertFormMilliSecToTime(startTime) + " am");
        }
        mTakenButton = findViewById(R.id.button_taken_count);
        onClickOnTakenButton(Long.parseLong(id));
        mIgnoreButton = findViewById(R.id.button_ignore_count);
        onClickOnIgnoreButton(Long.parseLong(id));
    }

    private void onClickOnTakenButton(final long id){
        mTakenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountUtilities.incrementTakenCount(DetailActivity.this,id);
            }
        });
    }

    private void onClickOnIgnoreButton(final long id){
        mIgnoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountUtilities.incrementIgnoreCount(DetailActivity.this,id);
            }
        });
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
        String dateFormat = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateValue);
        return simpleDateFormat.format(calendar.getTime());
    }

    private void getImageForTypeImageView(int type) {
        switch (type) {
            case MedEntry.MED_TYPE_CAPSULES:
                mMedTypeImageView.setImageResource(R.drawable.ic_capsule);
                break;
            case MedEntry.MED_TYPE_TABLETS:
                mMedTypeImageView.setImageResource(R.drawable.ic_tablet);
                break;
            case MedEntry.MED_TYPE_SYRUP:
                mMedTypeImageView.setImageResource(R.drawable.ic_syrup_liquid);
                break;
            case MedEntry.MED_TYPE_INHALER:
                mMedTypeImageView.setImageResource(R.drawable.ic_inhaler);
                break;
            case MedEntry.MED_TYPE_DROPS:
                mMedTypeImageView.setImageResource(R.drawable.ic_eye_drop);
                break;
            case MedEntry.MED_TYPE_OINTMENT:
                mMedTypeImageView.setImageResource(R.drawable.ic_ointiment);
                break;
            case MedEntry.MED_TYPE_INJECTION:
                mMedTypeImageView.setImageResource(R.drawable.ic_injection);
                break;
            case MedEntry.MED_TYPE_OTHERS:
                mMedTypeImageView.setImageResource(R.drawable.ic_other_meds);
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

}
