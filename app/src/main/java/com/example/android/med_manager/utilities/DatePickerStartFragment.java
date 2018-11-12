package com.example.android.med_manager.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.android.med_manager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SOLARIN O. OLUBAYODE on 04/04/18.
 */

public class DatePickerStartFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        getDate(year, month, day);
    }

    public void getDate(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = day + "/" + ++month + "/" + year;
        Date dateObject = null;
        String newDateString = null;
        try {
            dateObject = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDateString = dateFormat.format(dateObject);
        Log.i("TIMEPICKERFRAGMENT ", newDateString);
        setDate(newDateString);
    }

    public void setDate(String newDateString) {
//        String monthString = monthInString(month);
        EditText startDateEditText = getActivity().findViewById(R.id.med_start_date);
        startDateEditText.setText(newDateString);
    }
//    private String monthInString(int month) {
//        String monthString = null;
//        switch (month){
//            case 0:
//                monthString = "Jan";
//                break;
//            case 1:
//                monthString = "Feb";
//                break;
//            case 2:
//                monthString = "Mar";
//                break;
//            case 3:
//                monthString = "Apr";
//                break;
//            case 4:
//                monthString = "May";
//                break;
//            case 5:
//                monthString = "Jun";
//                break;
//            case 7:
//                monthString = "july";
//                break;
//            case 8:
//                monthString = "Aug";
//                break;
//            case 9:
//                monthString = "Sep";
//                break;
//            case 10:
//                monthString = "Nov";
//                break;
//            case 11:
//                monthString = "Dec";
//                break;
//            default:
//                break;
//        }
//        return monthString;
//    }
}
