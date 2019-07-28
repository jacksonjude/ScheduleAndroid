package com.jacksonjude.schedule.schedulemanager;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.j256.ormlite.dao.Dao;
import com.jacksonjude.schedule.activities.ScheduleInfoActivity;
import com.jacksonjude.schedule.ScheduleConstants;
import com.jacksonjude.schedule.model.Schedule;
import com.jacksonjude.schedule.model.ScheduleDatabaseHelper;
import com.jacksonjude.schedule.model.WeekSchedules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by jackson on 3/1/18.
 */

public class ScheduleInfoManager implements Observer {
    public static ScheduleInfoActivity mainActivity;

    public ScheduleInfoManager(ScheduleInfoActivity activity)
    {
        mainActivity = activity;
    }

    public ScheduleInfoManager()
    {

    }

    public void syncCloudData()
    {
        CloudManager.getInstance().addObserver(this);

        CloudManager.addToQueue(ScheduleConstants.scheduleRecordName);
        CloudManager.addToQueue(ScheduleConstants.weekSchedulesRecordName);

        CloudManager.initQueue();
    }

    @Override
    public void update(Observable observable, Object o) {
        Map<String, Object> dataArray = (HashMap<String, Object>) o;

        System.out.println("SIM: Update received");

        switch ((String) dataArray.get("type"))
        {
            case "syncedData":
                HashMap<String,Object> receivedObjects = (HashMap<String,Object>) dataArray.get("objects");
                saveData(receivedObjects);
        }
    }

    private void saveData(HashMap<String,Object> receivedObjects)
    {
        System.out.println("SIM: Saving data");

        ScheduleDatabaseHelper databaseHelper = mainActivity.getScheduleDatabaseHelper();

        Dao<Schedule, String> scheduleDao = databaseHelper.getScheduleDao();
        Dao<WeekSchedules, Long> weekSchedulesDao = databaseHelper.getWeekSchedulesDao();

        for (int i=0; i < ScheduleConstants.MODEL_TYPES.size(); i++)
        {
            String modelTypeOn = ScheduleConstants.MODEL_TYPES.get(i);

            ArrayList<Object> receivedObjectArray = (ArrayList<Object>) receivedObjects.get(modelTypeOn);

            if (receivedObjectArray != null) {
                switch (modelTypeOn) {
                    case "Schedule":
                        for (int j = 0; j < receivedObjectArray.size(); j++) {
                            Schedule receivedSchedule = (Schedule) receivedObjectArray.get(j);

                            //System.out.println("S: " + receivedSchedule.scheduleCode + " -- " + String.valueOf(j));

                            databaseHelper.loadArraysToJSONString(receivedSchedule);

                            try
                            {
                                if (scheduleDao.idExists(receivedSchedule.scheduleCode)) {
                                    scheduleDao.update(receivedSchedule);
                                } else {
                                    scheduleDao.create(receivedSchedule);
                                }
                            }
                            catch (java.sql.SQLException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        System.out.println("SIM: Received " + receivedObjectArray.size() + " Schedules");
                        break;
                    case "WeekSchedules":
                        for (int j = 0; j < receivedObjectArray.size(); j++) {
                            WeekSchedules receivedWeekSchedules = (WeekSchedules) receivedObjectArray.get(j);

                            databaseHelper.loadArraysToJSONString(receivedWeekSchedules);

                            try
                            {
                                if (receivedWeekSchedules.weekStartDate != null && weekSchedulesDao.idExists(receivedWeekSchedules.weekStartDate)) {
                                    weekSchedulesDao.update(receivedWeekSchedules);
                                } else {
                                    weekSchedulesDao.create(receivedWeekSchedules);
                                }
                            }
                            catch (java.sql.SQLException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        System.out.println("SIM: Received " + receivedObjectArray.size() + " WeekSchedules");
                        break;
                }
            }
        }

        System.out.println("SIM: Finished Syncing");

        Intent finishedSyncingIntent = new Intent("cloudFinishedSyncing");
        LocalBroadcastManager.getInstance(mainActivity).sendBroadcast(finishedSyncingIntent);
    }

    public String[] getCurrentPeriod()
    {
        Date todayDate = new Date();

        Schedule currentSchedule = getScheduleForDate(todayDate);

        if (currentSchedule == null)
        {
            return new String[]{ScheduleConstants.PERIOD_TYPE.NO_SCHOOL.toString()};
        }

        ArrayList<String> currentPeriodTimes = currentSchedule.periodTimes;
        ArrayList<Integer> currentPeriodNumbers = currentSchedule.periodNumbers;

        String lastPeriodEnd = null;

        for (int i=0;i<currentPeriodTimes.size();i++)
        {
            String[] periodRangeToTest = currentPeriodTimes.get(i).split("-");
            if (timeInRange(periodRangeToTest, todayDate))
            {
                return new String[]{ScheduleConstants.PERIOD_TYPE.PERIOD.toString(), String.valueOf(currentPeriodNumbers.get(i)), currentPeriodTimes.get(i)};
            }
            else if (lastPeriodEnd != null && timeInRange(new String[]{lastPeriodEnd, periodRangeToTest[0]}, todayDate))
            {
                return new String[]{ScheduleConstants.PERIOD_TYPE.PASSING_PERIOD.toString(), String.valueOf(currentPeriodNumbers.get(i)), lastPeriodEnd + periodRangeToTest[0]};
            }
            else if (i == 0 && todayDate.before(convertDate(periodRangeToTest[0], "HH:mm", new Date())))
            {
                return new String[]{ScheduleConstants.PERIOD_TYPE.BEFORE_SCHOOL.toString()};
            }
            else if (i+1 == currentPeriodTimes.size() && todayDate.after(convertDate(periodRangeToTest[1], "HH:mm", new Date())))
            {
                return new String[]{ScheduleConstants.PERIOD_TYPE.AFTER_SCHOOL.toString()};
            }

            lastPeriodEnd = periodRangeToTest[1];
        }

        return new String[]{ScheduleConstants.PERIOD_TYPE.NO_SCHOOL.toString()};
    }

    public String[] getSchoolStartEnd(Date dateToGet)
    {
        Schedule scheduleForSchoolStartEnd = getScheduleForDate(dateToGet);

        if (scheduleForSchoolStartEnd == null || scheduleForSchoolStartEnd.scheduleCode == "H" || scheduleForSchoolStartEnd.scheduleCode == "W" || scheduleForSchoolStartEnd.scheduleCode == "L" || scheduleForSchoolStartEnd.periodTimes == null || scheduleForSchoolStartEnd.periodTimes.size() == 0)
        {
            return new String[] {ScheduleConstants.SCHOOL_TIMES_STATUS.NO_SCHOOL.toString()};
        }

        String schoolStart = scheduleForSchoolStartEnd.periodTimes.get(0).split("-")[0];
        String schoolEnd = scheduleForSchoolStartEnd.periodTimes.get(scheduleForSchoolStartEnd.periodTimes.size()-1).split("-")[1];

        Boolean schoolStarted = (new Date()).after(convertDate(schoolStart, "HH:mm", new Date()));
        Boolean schoolEnded = (new Date()).after(convertDate(schoolEnd, "HH:mm", new Date()));

        return new String[] {ScheduleConstants.SCHOOL_TIMES_STATUS.SCHOOL_TIMES.toString(), schoolStart, schoolEnd, schoolStarted.toString(), schoolEnded.toString()};
    }

    public String[] getNextSchoolStartEnd()
    {
        Date dateOn = addToDate(new Date(), Calendar.DATE, 1);
        int timesRun = 0;
        while (true)
        {
            String[] schoolStartEndInfo = getSchoolStartEnd(dateOn);
            if (schoolStartEndInfo[0] == ScheduleConstants.SCHOOL_TIMES_STATUS.SCHOOL_TIMES.toString())
            {
                return new String[]{ScheduleConstants.SCHOOL_TIMES_STATUS.SCHOOL_TIMES.toString(), schoolStartEndInfo[1], schoolStartEndInfo[2], new SimpleDateFormat("EEE, MM/dd").format(dateOn)};
            }

            dateOn = addToDate(dateOn, Calendar.DATE, 1);
            timesRun++;

            if (timesRun > 200)
            {
                return new String[]{ScheduleConstants.SCHOOL_TIMES_STATUS.NO_SCHOOL.toString()};
            }
        }
    }

    private Date addToDate(Date dateToAdd, int calenderField, int amount)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToAdd);
        calendar.add(calenderField, amount);
        return calendar.getTime();
    }

    public Schedule getScheduleForDate(Date dateToGet)
    {
        Date firstDayOfWeekToGet = getFirstDayOfWeek(dateToGet);

        ScheduleDatabaseHelper databaseHelper = mainActivity.getScheduleDatabaseHelper();
        WeekSchedules currentWeekSchedules = databaseHelper.getWeekSchedulesObject(getDateLong(firstDayOfWeekToGet));

        int currentDayOfWeek = (int) getCalenderField(dateToGet, Calendar.DAY_OF_WEEK);

        if (currentWeekSchedules == null || currentDayOfWeek > 6 || currentDayOfWeek < 2)
        {
            return null;
        }

        String scheduleCode = currentWeekSchedules.schedules.get(currentDayOfWeek-2);

        return databaseHelper.getScheduleObject(scheduleCode);
    }

    private long getDateLong(Date dateToGet)
    {
        Calendar today = Calendar.getInstance();
        today.setTime(dateToGet);

        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 12);

        return today.getTime().getTime();
    }

    private Date getFirstDayOfWeek(Date dateToGet)
    {
        Calendar today = Calendar.getInstance();
        today.setTime(dateToGet);

        today.set(Calendar.DAY_OF_WEEK, 1);

        return today.getTime();
    }

    public Object getCalenderField(Date dateToGet, int calenderField)
    {
        Calendar today = Calendar.getInstance();
        today.setTime(dateToGet);

        return today.get(calenderField);
    }

    private Boolean timeInRange(String[] timeRange, Date timeToTest)
    {
        try {
            String timeToTestString = new SimpleDateFormat("HH:mm").format(timeToTest);
            Date d = new SimpleDateFormat("HH:mm").parse(timeToTestString);
            Calendar calendarTest = Calendar.getInstance();
            calendarTest.setTime(d);
            //calendarTest.add(Calendar.DATE, 1);

            String rangeStart = timeRange[0];
            Date timeStart = new SimpleDateFormat("HH:mm").parse(rangeStart);
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(timeStart);
            calendarStart.set(Calendar.YEAR, calendarTest.get(Calendar.YEAR));
            calendarStart.set(Calendar.MONTH, calendarTest.get(Calendar.MONTH));
            calendarStart.set(Calendar.DAY_OF_MONTH, calendarTest.get(Calendar.DAY_OF_MONTH));

            String rangeEnd = timeRange[1];
            Date timeEnd = new SimpleDateFormat("HH:mm").parse(rangeEnd);
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(timeEnd);
            //calendarEnd.add(Calendar.DATE, 1);
            calendarEnd.set(Calendar.YEAR, calendarTest.get(Calendar.YEAR));
            calendarEnd.set(Calendar.MONTH, calendarTest.get(Calendar.MONTH));
            calendarEnd.set(Calendar.DAY_OF_MONTH, calendarTest.get(Calendar.DAY_OF_MONTH));

            System.out.println(rangeStart + "--" + rangeEnd + "--" + timeToTestString);

            Date x = calendarTest.getTime();
            if (x.after(calendarStart.getTime()) && x.before(calendarEnd.getTime())) {
                System.out.println(true);
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Date convertDate(String timeString, String dateFormat, Date dateDefault)
    {
        try
        {
            Date dateFormatted = new SimpleDateFormat(dateFormat).parse(timeString);
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(dateFormatted);

            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(dateDefault);

            timeCalendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
            timeCalendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
            timeCalendar.set(Calendar.DATE, dateCalendar.get(Calendar.DATE));

            return timeCalendar.getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
