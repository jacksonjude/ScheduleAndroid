package com.jacksonjude.schedule.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.jacksonjude.schedule.R;
import com.jacksonjude.schedule.ScheduleConstants;
import com.jacksonjude.schedule.model.Schedule;
import com.jacksonjude.schedule.model.ScheduleDatabaseHelper;
import com.jacksonjude.schedule.model.UserSchedule;
import com.jacksonjude.schedule.model.WeekSchedules;
import com.jacksonjude.schedule.schedulemanager.CloudManager;
import com.jacksonjude.schedule.schedulemanager.ScheduleInfoManager;

import net.moddity.droidnubekit.*;

import java.util.Date;

public class ScheduleInfoActivity extends OrmLiteBaseActivity<ScheduleDatabaseHelper> {
    private ScheduleInfoManager infoManager;
    boolean cloudSyncOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);


        final Button calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transitionToCalendarActivity();
            }
        });

        final Button userScheduleButton = findViewById(R.id.userScheduleButton);
        userScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transitionToUserScheduleActivity();
            }
        });

        System.out.println("MA: Init");

        DroidNubeKit.initNube(
                ScheduleConstants.cloudKitAPIToken, //your api token
                ScheduleConstants.cloudKitIdentifier,
                DroidNubeKitConstants.kEnvironmentType.kProductionEnvironment, //development or production
                this
        );

        DroidNubeKit.getInstance().modelClasses.add(Schedule.class);
        DroidNubeKit.getInstance().modelClasses.add(WeekSchedules.class);
        DroidNubeKit.getInstance().modelClasses.add(UserSchedule.class);

        CloudManager.activity = this;

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("cloudFinishedSyncing"));
        infoManager = new ScheduleInfoManager(this);
        fetchScheduleInfo(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scheduleinfo_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchScheduleInfo(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void transitionToCalendarActivity()
    {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    private void transitionToUserScheduleActivity()
    {
        Intent intent = new Intent(this, UserScheduleActivity.class);
        startActivity(intent);
    }

    public ScheduleDatabaseHelper getScheduleDatabaseHelper()
    {
        return getHelper();
    }

    public void fetchScheduleInfo(boolean shouldDownloadCloudData)
    {
        ((TextView) this.findViewById(R.id.currentPeriod)).setText(R.string.loading_message);
        ((TextView) this.findViewById(R.id.schoolStartEnd)).setText(R.string.loading_message);
        ((TextView) this.findViewById(R.id.tomorrowSchoolStartEnd)).setText(R.string.loading_message);

        if (shouldDownloadCloudData && cloudSyncOn)
        {
            downloadCloudData();
        }
        else
        {
            refreshScheduleInfo();
        }
    }

    public void downloadCloudData()
    {
        System.out.println("MA: Syncing Cloud Data");

        infoManager.syncCloudData();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction())
            {
                case "cloudFinishedSyncing":
                    refreshScheduleInfo();
                    break;
            }
        }
    };

    public void refreshScheduleInfo()
    {
        String[] currentPeriodInfo = infoManager.getCurrentPeriod();
        displayCurrentPeriod(currentPeriodInfo);

        String[] schoolStartEndInfo = infoManager.getSchoolStartEnd(new Date());
        displayCurrentSchoolStartEnd(schoolStartEndInfo);

        String[] tomorrowSchoolStartEndInfo = infoManager.getNextSchoolStartEnd();
        displayNextSchoolStartEnd(tomorrowSchoolStartEndInfo);
    }

    public void displayCurrentPeriod(String[] currentPeriodInfo)
    {
        String stringToDisplay;

        switch (ScheduleConstants.PERIOD_TYPE.valueOf(currentPeriodInfo[0])) {
            case PERIOD:
                stringToDisplay = "The current period is " + currentPeriodInfo[1] + "\n" + currentPeriodInfo[2];
                break;
            case PASSING_PERIOD:
                stringToDisplay = "Passing Period\nThe next period is " + currentPeriodInfo[1] + "\n" + currentPeriodInfo[2];
                break;
            case BEFORE_SCHOOL:
                stringToDisplay = "School has not started yet";
                break;
            case AFTER_SCHOOL:
                stringToDisplay = "School has ended";
                break;
            case NO_SCHOOL:
                stringToDisplay = "No school today";
                break;
            default:
                stringToDisplay = "Error";
                break;
        }

        System.out.println(stringToDisplay);

        ((TextView)this.findViewById(R.id.currentPeriod)).setText(stringToDisplay);
    }

    public void displayCurrentSchoolStartEnd(String[] schoolStartEndInfo)
    {
        String stringToDisplay;

        switch (ScheduleConstants.SCHOOL_TIMES_STATUS.valueOf(schoolStartEndInfo[0])) {
            case SCHOOL_TIMES:
                stringToDisplay = "School start" + (Boolean.valueOf(schoolStartEndInfo[3]) ? "ed" : "s") + " today at " + schoolStartEndInfo[1] + "\nand end" + (Boolean.valueOf(schoolStartEndInfo[4]) ? "ed" : "s") + " today at " + schoolStartEndInfo[2];
                break;
            case NO_SCHOOL:
                stringToDisplay = "No school today";
                break;
            default:
                stringToDisplay = "Error";
                break;
        }

        System.out.println(stringToDisplay);

        ((TextView)this.findViewById(R.id.schoolStartEnd)).setText(stringToDisplay);
    }

    public void displayNextSchoolStartEnd(String[] nextSchoolStartEndInfo)
    {
        String stringToDisplay;

        switch (ScheduleConstants.SCHOOL_TIMES_STATUS.valueOf(nextSchoolStartEndInfo[0])) {
            case SCHOOL_TIMES:
                stringToDisplay = "School starts at " + nextSchoolStartEndInfo[1]/* + "\nand ends at " + nextSchoolStartEndInfo[2]*/ + "\non " + nextSchoolStartEndInfo[3];
                break;
            case NO_SCHOOL:
                stringToDisplay = "No school";
                break;
            default:
                stringToDisplay = "Error";
                break;
        }

        System.out.println(stringToDisplay);

        ((TextView)this.findViewById(R.id.tomorrowSchoolStartEnd)).setText(stringToDisplay);
    }
}
