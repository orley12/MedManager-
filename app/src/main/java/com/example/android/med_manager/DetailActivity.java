package com.example.android.med_manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.med_manager.data.MedContract.MedEntry;

public class DetailActivity extends AppCompatActivity {

    TextView mMedNameTextView;

    ImageView mMedTypeImageView;

    TextView mMedDescriptionTextView;

    TextView mMedDosageTextView;

    TextView mMedIntervalTextView;

    TextView mMedStartDateTextView;

    TextView mMedEndDateTextView;

    TextView mMedNameSmallTextView;

    TextView mMedTypeTextView;

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
        mMedStartDateTextView.setText(startDate);
        mMedEndDateTextView = findViewById(R.id.end_date_detail);
        mMedEndDateTextView.setText(endDate);
        mMedNameSmallTextView = findViewById(R.id.med_name_small_detail);
        mMedNameSmallTextView.setText(name);

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
