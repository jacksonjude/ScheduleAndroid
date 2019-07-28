package com.jacksonjude.schedule.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.jacksonjude.schedule.R;
import com.jacksonjude.schedule.schedulemanager.UserScheduleManager;

public class UserScheduleActivity extends AppCompatActivity {

    UserScheduleAdapter userScheduleAdapter;
    UserScheduleManager userScheduleManager;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                userScheduleManager.closingUserScheduleActivity();
                finish();
            }
        });

        if (getActionBar() != null)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
        }

        userScheduleManager = new UserScheduleManager(this);
        if (!userScheduleManager.fetchUserScheduleData())
        {
            displayCreateNewUserID();
        }

        listview = findViewById(R.id.userScheduleListView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String periodName = (String)parent.getItemAtPosition(position);
                displayChangePeriodNameAlert(periodName, position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userschedule_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void setUserSchedule(String[] userSchedule)
    {
        if (userScheduleAdapter != null)
        {
            userScheduleAdapter.setData(userSchedule);
        }
        else
        {
            userScheduleAdapter = new UserScheduleAdapter(this, userSchedule);
        }

        listview.setAdapter(userScheduleAdapter);
    }

    public void displayChangePeriodNameAlert(final String currentPeriodName, final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setHint(currentPeriodName);
        builder.setView(input);

        builder.setTitle("Change Period Name");

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedPeriodName = input.getText().toString();
                if (updatedPeriodName != "")
                {
                    String[] userScheduleData = userScheduleAdapter.data;
                    userScheduleData[position] = updatedPeriodName;
                    setUserSchedule(userScheduleData);

                    userScheduleManager.changedUserSchedule = true;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void displayCreateNewUserID()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setHint("UserID");
        builder.setView(input);

        builder.setTitle("Set User ID");
        builder.setMessage("Create or enter a user id");

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredUserID = input.getText().toString();
                if (enteredUserID != "")
                {
                    userScheduleManager.setUserID(enteredUserID);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
