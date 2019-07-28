package com.jacksonjude.schedule.schedulemanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.jacksonjude.schedule.ScheduleConstants;
import com.jacksonjude.schedule.activities.UserScheduleActivity;
import com.jacksonjude.schedule.model.UserSchedule;

import net.moddity.droidnubekit.DroidNubeKit;
import net.moddity.droidnubekit.DroidNubeKitConstants;
import net.moddity.droidnubekit.requests.DNKCallback;
import net.moddity.droidnubekit.requests.DNKRecordQueryRequest;
import net.moddity.droidnubekit.utils.DNKFieldTypes;
import net.moddity.droidnubekit.utils.DNKOperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class UserScheduleManager extends Observable implements DNKCallback {

    private UserScheduleActivity userScheduleActivity;
    public boolean changedUserSchedule = false;
    private UserSchedule userSchedule;

    public UserScheduleManager(UserScheduleActivity activity)
    {
        userScheduleActivity = activity;
    }

    public boolean fetchUserScheduleData()
    {
        String userID = getUserID();
        if (userID != null) {
            DNKRecordQueryRequest request = DNKRecordQueryRequest.queryWithRecordName(ScheduleConstants.userScheduleRecordName);
            request.addFilter("userID", userID, DNKFieldTypes.STRING.toString(), DroidNubeKitConstants.EQUALS);

            DroidNubeKit.fetchRecordsByQuery(request, DroidNubeKitConstants.kDatabaseType.kPublicDatabase, this);
            return true;
        }

        return false;
    }

    public void setUserScheduleData(String[] userScheduleData)
    {

    }

    public void setUserID(String userID)
    {
        SharedPreferences sharedPreferences = userScheduleActivity.getSharedPreferences("SchedulePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", userID);
        editor.apply();
    }

    private String getUserID()
    {
        SharedPreferences sharedpreferences = userScheduleActivity.getSharedPreferences("SchedulePreferences", Context.MODE_PRIVATE);
        return sharedpreferences.getString("userID", null);
    }

    @Override
    public void success(Object o) {
        System.out.println("USM: Found UserSchedule");
        if (((ArrayList<UserSchedule>)o).size() > 0)
        {
            System.out.println(((ArrayList<?>)o));
            ArrayList<String> userScheduleArrayList = ((ArrayList<UserSchedule>)o).get(0).periodNames;

            userScheduleActivity.setUserSchedule(convertArrayListToArray(userScheduleArrayList));

            userSchedule = ((ArrayList<UserSchedule>)o).get(0);
        }
        else
        {
            userScheduleActivity.setUserSchedule(createNewUserSchedule());
            userSchedule = new UserSchedule();
            userSchedule.periodNames = convertArrayToArrayList(createNewUserSchedule());
            userSchedule.userID = getUserID();
        }
    }

    private String[] createNewUserSchedule()
    {
        String[] userSchedule = new String[ScheduleConstants.periodsInFullDay];

        for (int i=0;i<ScheduleConstants.periodsInFullDay;i++)
        {
            //userSchedule = push(userSchedule, "Period " + i);
            userSchedule[i] = "Period " + i;
        }

        return userSchedule;
    }

    private String[] convertArrayListToArray(ArrayList<String> arrayList)
    {
        String[] array = {};
        for (int i=0;i<arrayList.size();i++)
        {
            array = push(array, arrayList.get(i));
        }

        return array;
    }

    private ArrayList<String> convertArrayToArrayList(String[] array)
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i=0;i<array.length;i++)
        {
            arrayList.add(array[i]);
        }

        return arrayList;
    }

    private static String[] push(String[] array, String push) {
        String[] longer = new String[array.length + 1];
        for (int i = 0; i < array.length; i++)
            longer[i] = array[i];
        longer[array.length] = push;
        return longer;
    }

    @Override
    public void failure(Throwable exception) {

    }

    public void closingUserScheduleActivity()
    {
        if (changedUserSchedule)
        {
            //System.out.println("USM: MOD");
            DroidNubeKit.modifyRecord(userSchedule, DNKOperationType.UPDATE, DroidNubeKitConstants.kDatabaseType.kPublicDatabase, new DNKCallback<List<UserSchedule>>() {
                @Override
                public void success(List<UserSchedule> userSchedules) {
                    System.out.println("Success");
                }

                @Override
                public void failure(Throwable exception) {
                    exception.printStackTrace();
                }
            });
        }
    }
}
