package com.jacksonjude.schedule.schedulemanager;

import com.jacksonjude.schedule.ScheduleConstants;
import com.jacksonjude.schedule.model.Schedule;

import java.util.Calendar;
import java.util.Date;

public class CalendarManager {
    private ScheduleInfoManager infoManager;

    public CalendarManager()
    {
        infoManager = new ScheduleInfoManager();
    }

    public String getScheduleCodeForDay(int year, int month, int dayOfMonth)
    {
        Date date = getDateYMD(year, month, dayOfMonth);

        Schedule schedule = infoManager.getScheduleForDate(date);

        int currentDayOfWeek = (int) infoManager.getCalenderField(date, Calendar.DAY_OF_WEEK);

        if (schedule != null)
        {
            return schedule.scheduleCode;
        }
        else if (currentDayOfWeek > 6 || currentDayOfWeek < 2)
        {
            return "W";
        }
        else
        {
            return "L";
        }
    }

    public String getSelectedDayStartEnd(int year, int month, int dayOfMonth)
    {
        Date date = getDateYMD(year, month, dayOfMonth);

        String[] selectedDateStartEndInfo = infoManager.getSchoolStartEnd(date);

        if (selectedDateStartEndInfo[0].equals(ScheduleConstants.SCHOOL_TIMES_STATUS.SCHOOL_TIMES.toString()))
        {
            return selectedDateStartEndInfo[1] + " - " + selectedDateStartEndInfo[2];
        }
        else
        {
            return "No School!";
        }
    }

    private Date getDateYMD(int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 12);

        return calendar.getTime();
    }
}
