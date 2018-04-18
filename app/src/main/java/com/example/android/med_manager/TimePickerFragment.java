package com.example.android.med_manager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SOLARIN O. OLUBAYODE on 04/04/18.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        getTime(hourOfDay, minute);
    }

    public void getTime(int hourOfDay, int minute) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String dateString = hourOfDay + ":" + minute;
        Date dateObject = null;
        String newDateString = null;
        try {
            dateObject = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDateString = dateFormat.format(dateObject);
        Log.i("TIMEPICKERFRAGMENT ", newDateString);
        setTime(newDateString, hourOfDay);
    }

    public void setTime(String newDateString, int hourOfDay) {
        EditText startTimeEditText = getActivity().findViewById(R.id.start_time_edit_text);
        String timeOfDay;
        if (hourOfDay > 12) {
            timeOfDay = "pm";
        } else {
            timeOfDay = "am";
        }
        startTimeEditText.setText(newDateString + " " + timeOfDay);
    }

}
