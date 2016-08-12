package ru.comtrans.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.comtrans.helpers.Const;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DateListener mListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        String extraDate = getArguments().getString(Const.EXTRA_DATE);
        int year,month,day;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        if(extraDate==null) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }else {
            SimpleDateFormat format = new SimpleDateFormat(Const.INFO_BLOCK_DATE_FORMAT, Locale.getDefault());
            try {
                Date date = format.parse(extraDate);
                c.setTime(date);
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        //Get the DatePicker instance from DatePickerDialog
            DatePicker dp = dpd.getDatePicker();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);



        return dpd;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        mListener.setDate(c);
        getDialog().dismiss();
    }

    public void setListener(DateListener listener) {
        mListener = listener;
    }

    public interface DateListener {
        void setDate(Calendar date);
    }
}
