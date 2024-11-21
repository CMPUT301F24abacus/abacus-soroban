package com.example.soroban.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.example.soroban.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Dialog fragment that allows a user to pick a specific date.
 * @Author: Matthieu Larochelle (Referencing: https://developer.android.com/develop/ui/views/components/pickers)
 * @Version: 1.0
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private DatePickerListener listener;
    private Date targetDate;

    public void setListener(DatePickerListener listener) {
        this.listener = listener;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Default time is set to the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // New instance of DatePickerDialog with custom theme
        return new DatePickerDialog(requireContext(), R.style.CustomDatePickerDialog, this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Once user has set date
        if (targetDate != null && listener != null) {
            Date newDate = new GregorianCalendar(year, month, day).getTime();
            listener.setDate(targetDate, newDate);
        }
    }
}
