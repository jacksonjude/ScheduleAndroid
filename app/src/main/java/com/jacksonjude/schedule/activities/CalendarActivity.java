package com.jacksonjude.schedule.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.jacksonjude.schedule.R;
import com.jacksonjude.schedule.schedulemanager.CalendarManager;
import com.jacksonjude.schedule.schedulemanager.CloudManager;
import com.jacksonjude.schedule.schedulemanager.ScheduleInfoManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    private CalendarManager calendarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        if (getActionBar() != null)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
        }

        calendarManager = new CalendarManager();

        final CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                setInfoForSelectedDate(year, month, dayOfMonth);
            }
        });

        Calendar today = Calendar.getInstance();
        today.setTime(new Date(calendarView.getDate()));

        setInfoForSelectedDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println(item.getItemId());
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setInfoForSelectedDate(int year, int month, int dayOfMonth)
    {
        String selectedDayScheduleCode = calendarManager.getScheduleCodeForDay(year, month, dayOfMonth);
        String selectedDayStartEnd = calendarManager.getSelectedDayStartEnd(year, month, dayOfMonth);
        displaySelectedDateInfo(year, month, dayOfMonth, selectedDayScheduleCode, selectedDayStartEnd);
    }

    public void displaySelectedDateInfo(int year, int month, int dayOfMonth, String selectedDayScheduleCode, String selectedDayStartEnd)
    {
        final TextView infoTextView = findViewById(R.id.selectedDateInfoTextView);
        Calendar selectedDayCalendar = Calendar.getInstance();
        selectedDayCalendar.set(year, month, dayOfMonth);

        String formattedDateString = new SimpleDateFormat("MM/dd").format(selectedDayCalendar.getTime());

        String stringToDisplay = formattedDateString + " -- " + selectedDayScheduleCode + "\n" + selectedDayStartEnd;
        infoTextView.setText(stringToDisplay);
    }
}
