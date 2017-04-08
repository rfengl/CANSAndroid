package my.com.cans.cansandroid.controls;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import my.com.cans.cansandroid.R;
import my.com.cans.cansandroid.managers.Convert;
import my.com.cans.cansandroid.managers.ValidateManager;
import my.com.cans.cansandroid.objects.MyApp;

/**
 * Created by Rfeng on 02/09/16.
 */
public class DateTimePicker extends CustomEditText {
    public DateTimePicker(Context context) {
        super(context);
        init(context);
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.setFocusable(false);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            showDateTimePicker();
        }
    }

    private boolean mIsTimeEnabled = false;

    public void setTimeEnabled(boolean enable) {
        mIsTimeEnabled = enable;
        setDate(date);
    }

    Calendar date;

    public void showDateTimePicker() {
        final Context context = this.getContext();
        String dateString = this.getText().toString();
        date = Calendar.getInstance();
        if (!ValidateManager.isEmptyOrNull(dateString))
            date.setTime(new Convert(dateString).to(Date.class));

        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);

                setDate(date);
                if (mIsTimeEnabled) {
//                    DateTimePicker.this.setText(new Convert(date.getTime()).to(String.class));
                    new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            date.set(Calendar.MINUTE, minute);
                            DateTimePicker.this.setDate(date);
//                            DateTimePicker.this.setText(new Convert(date.getTime()).to(String.class));
                        }
                    }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
                }
//                else {
//                    DateTimePicker.this.setText(new SimpleDateFormat(MyApp.getContext().getString(R.string.date_format)).format(date.getTime()));
//                }
            }

        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE)).show();
    }

    public void setDate(Date date) {
        setDate(new Convert(date).to(Calendar.class));
    }

    public void setDate(Calendar date) {
        this.date = date;
        if (date == null)
            this.setText("");
        else if (mIsTimeEnabled)
            this.setText(new Convert(date.getTime()).to(String.class));
        else
            this.setText(new SimpleDateFormat(MyApp.getContext().getString(R.string.date_format)).format(date.getTime()));
    }

    public Date getDate() {
        return new Convert(this.getText().toString()).to(Date.class);
    }
}
