package com.example.hairsalon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int Hours = calendar.get(Calendar.HOUR_OF_DAY);
        int Minutes = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,(TimePickerDialog.OnTimeSetListener) getActivity(),Hours,Minutes, DateFormat.is24HourFormat(getActivity()));
    }
}

