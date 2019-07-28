package com.jacksonjude.schedule.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.*;

import java.util.ArrayList;

import static java.lang.System.out;

/**
 * Created by jackson on 3/2/18.
 */

public class ScheduleDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "BellSchedule.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Schedule, String> scheduleDao = null;
    private RuntimeExceptionDao<Schedule, String> scheduleRuntimeDao = null;
    private Dao<WeekSchedules, Long> weekSchedulesDao = null;
    private RuntimeExceptionDao<WeekSchedules, Long> weekSchedulesRuntimeDao = null;
    private Dao<UserSchedule, String> userScheduleDao = null;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Schedule.class);
            TableUtils.createTable(connectionSource, WeekSchedules.class);
        } catch (java.sql.SQLException e) {
            out.println(ScheduleDatabaseHelper.class.getName() + " -- Can't create database -- " + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    public Dao<Schedule, String> getScheduleDao() {
        if (scheduleDao == null) {
            try
            {
                scheduleDao = getDao(Schedule.class);
            }
            catch (java.sql.SQLException e)
            {
                out.println(e);
            }
        }
        return scheduleDao;
    }

    public RuntimeExceptionDao<Schedule, String> getScheduleRuntimeDao() {
        if (scheduleRuntimeDao == null) {
            try
            {
                scheduleRuntimeDao = getDao(Schedule.class);
            }
            catch (java.sql.SQLException e)
            {
                out.println(e);
            }
        }
        return scheduleRuntimeDao;
    }

    public Dao<WeekSchedules, Long> getWeekSchedulesDao() {
        if (weekSchedulesDao == null) {
            try
            {
                weekSchedulesDao = getDao(WeekSchedules.class);
            }
            catch (java.sql.SQLException e)
            {
                out.println(e);
            }
        }
        return weekSchedulesDao;
    }

    public RuntimeExceptionDao<WeekSchedules, Long> getWeekSchedulesRuntimeDao() {
        if (weekSchedulesRuntimeDao == null) {
            try
            {
                weekSchedulesRuntimeDao = getDao(WeekSchedules.class);
            }
            catch (java.sql.SQLException e)
            {
                out.println(e);
            }
        }
        return weekSchedulesRuntimeDao;
    }

    public Dao<UserSchedule, String> getUserScheduleDao() {
        if (userScheduleDao == null) {
            try
            {
                userScheduleDao = getDao(UserSchedule.class);
            }
            catch (java.sql.SQLException e)
            {
                out.println(e);
            }
        }
        return userScheduleDao;
    }

    @Override
    public void close() {
        super.close();
        scheduleDao = null;
        scheduleRuntimeDao = null;
        weekSchedulesDao = null;
        weekSchedulesRuntimeDao = null;
    }

    public ScheduleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void loadArraysToJSONString(Object modelObject)
    {
        String[] splitObjectName = modelObject.getClass().getName().split("\\.");
        String shortObjectName = splitObjectName[splitObjectName.length-1];

        switch (shortObjectName)
        {
            case "Schedule":
                Schedule scheduleObject = (Schedule) modelObject;

                JSONArray periodNumbersJSONArray = new JSONArray(scheduleObject.periodNumbers);
                scheduleObject.periodNumbersJSON = periodNumbersJSONArray.toString();

                JSONArray periodTimesJSONArray = new JSONArray(scheduleObject.periodTimes);
                scheduleObject.periodTimesJSON = periodTimesJSONArray.toString();

                break;
            case "WeekSchedules":
                WeekSchedules weekSchedulesObject = (WeekSchedules) modelObject;

                JSONArray schedulesJSONArray = new JSONArray(weekSchedulesObject.schedules);
                weekSchedulesObject.schedulesJSON = schedulesJSONArray.toString();

                break;
        }
    }

    public void loadJSONStringsToArrays(Object modelObject) throws JSONException {
        if (modelObject == null)
        {
            return;
        }
        String[] splitObjectName = modelObject.getClass().getName().split("\\.");
        String shortObjectName = splitObjectName[splitObjectName.length-1];

        switch (shortObjectName)
        {
            case "Schedule":
                Schedule scheduleObject = (Schedule) modelObject;

                JSONArray periodNumbersJSONArray = new JSONArray(scheduleObject.periodNumbersJSON);
                scheduleObject.periodNumbers = JSONArrayToArrayList(periodNumbersJSONArray);

                JSONArray periodTimesJSONArray = new JSONArray(scheduleObject.periodTimesJSON);
                scheduleObject.periodTimes = JSONArrayToArrayList(periodTimesJSONArray);

                break;
            case "WeekSchedules":
                WeekSchedules weekSchedulesObject = (WeekSchedules) modelObject;

                JSONArray schedulesJSONArray = new JSONArray(weekSchedulesObject.schedulesJSON);
                weekSchedulesObject.schedules = JSONArrayToArrayList(schedulesJSONArray);

                break;
            case "UserSchedule":
                UserSchedule userScheduleObject = (UserSchedule) modelObject;

                JSONArray periodNames = new JSONArray(userScheduleObject.periodNames);
        }
    }

    private ArrayList JSONArrayToArrayList(JSONArray jArray) throws JSONException {
        ArrayList listdata = new ArrayList<>();
        if (jArray != null)
        {
            for (int i=0;i<jArray.length();i++)
            {
                listdata.add(jArray.getString(i));
            }
        }

        return listdata;
    }

    public Schedule getScheduleObject(String id)
    {
        if (id != null && id != "")
        {
            try
            {
                if (getScheduleDao().idExists(id))
                {
                    Schedule scheduleObject = getScheduleDao().queryForId(id);
                    loadJSONStringsToArrays(scheduleObject);
                    return scheduleObject;
                }
            }
            catch (java.sql.SQLException | org.json.JSONException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public WeekSchedules getWeekSchedulesObject(long id)
    {
        if (id != 0)
        {
            try
            {
                if (getWeekSchedulesDao().idExists(id))
                {
                    WeekSchedules weekSchedulesObject = getWeekSchedulesDao().queryForId(id);
                    loadJSONStringsToArrays(weekSchedulesObject);
                    return weekSchedulesObject;
                }
            }
            catch (java.sql.SQLException | org.json.JSONException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

}
