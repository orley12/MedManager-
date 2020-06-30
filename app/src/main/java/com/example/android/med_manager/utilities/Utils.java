package com.example.android.med_manager.utilities;

import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map getPropertiesForMedTypeImageView(int type) {

        Map<String, java.io.Serializable> MedTypeProperties = new HashMap<>();
        switch (type) {
            case MedContract.MedEntry.MED_TYPE_CAPSULES:
                MedTypeProperties.put("image", R.drawable.ic_capsule);
                MedTypeProperties.put("backgroundColor", R.color.capsule_color);
                MedTypeProperties.put("text", "Capsule");
                break;
            case MedContract.MedEntry.MED_TYPE_TABLETS:
                MedTypeProperties.put("image", R.drawable.ic_tablet);
                MedTypeProperties.put("backgroundColor", R.color.tablet_color);
                MedTypeProperties.put("text", "Tablet");
                break;
            case MedContract.MedEntry.MED_TYPE_SYRUP:
                MedTypeProperties.put("image", R.drawable.ic_syrup_liquid);
                MedTypeProperties.put("backgroundColor", R.color.syrup_color);
                MedTypeProperties.put("text", "Syrup");
                break;
            case MedContract.MedEntry.MED_TYPE_INHALER:
                MedTypeProperties.put("image", R.drawable.ic_inhaler);
                MedTypeProperties.put("backgroundColor", R.color.inhaler_color);
                MedTypeProperties.put("text", "Inhaler");
                break;
            case MedContract.MedEntry.MED_TYPE_DROPS:
                MedTypeProperties.put("image", R.drawable.ic_drops);
                MedTypeProperties.put("backgroundColor", R.color.drops_color);
                MedTypeProperties.put("text", "Drops");
                break;
            case MedContract.MedEntry.MED_TYPE_OINTMENT:
                MedTypeProperties.put("image", R.drawable.ic_ointment);
                MedTypeProperties.put("backgroundColor", R.color.ointment_color);
                MedTypeProperties.put("text", "Ointment");
                break;
            case MedContract.MedEntry.MED_TYPE_INJECTION:
                MedTypeProperties.put("image", R.drawable.ic_injection);
                MedTypeProperties.put("backgroundColor", R.color.injection_color);
                MedTypeProperties.put("text", "Injection");
                break;
            case MedContract.MedEntry.MED_TYPE_OTHERS:
                MedTypeProperties.put("image", R.drawable.ic_other_meds);
                MedTypeProperties.put("backgroundColor", R.color.other_meds_color);
                MedTypeProperties.put("text", "Other Medications");
                break;
        }
        return MedTypeProperties;
    }
//
//    private void setPropertyForTypeTextView(int type) {
//        switch (type) {
//            case MedContract.MedEntry.MED_TYPE_CAPSULES:
//                mMedTypeTextView.setText("Capusles");
//                break;
//            case MedContract.MedEntry.MED_TYPE_TABLETS:
//                mMedTypeTextView.setText("Tablets");
//                break;
//            case MedContract.MedEntry.MED_TYPE_SYRUP:
//                mMedTypeTextView.setText("Syrup");
//                break;
//            case MedContract.MedEntry.MED_TYPE_INHALER:
//                mMedTypeTextView.setText("Inhaler");
//                break;
//            case MedContract.MedEntry.MED_TYPE_DROPS:
//                mMedTypeTextView.setText("Drops");
//                break;
//            case MedContract.MedEntry.MED_TYPE_OINTMENT:
//                mMedTypeTextView.setText("Ointment");
//                break;
//            case MedContract.MedEntry.MED_TYPE_INJECTION:
//                mMedTypeTextView.setText("Injection");
//                break;
//            case MedContract.MedEntry.MED_TYPE_OTHERS:
//                mMedTypeTextView.setText("Other Medications");
//                break;
//        }
//    }

    public static String convertFormMilliSecToTime(long date) {
        String dateFormat = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String convertFormMilliSecToDate(long date) {
        String dateFormat = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return simpleDateFormat.format(calendar.getTime());
    }
}
