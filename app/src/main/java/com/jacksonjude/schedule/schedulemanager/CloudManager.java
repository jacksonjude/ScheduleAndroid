package com.jacksonjude.schedule.schedulemanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import net.moddity.droidnubekit.DroidNubeKit;
import net.moddity.droidnubekit.DroidNubeKitConstants;
import net.moddity.droidnubekit.requests.DNKCallback;
import net.moddity.droidnubekit.requests.DNKRecordQueryRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Created by jackson on 3/1/18.
 */

public class CloudManager extends Observable implements DNKCallback {
    private static CloudManager instance;
    private ArrayList<String> fetchAllDataQueue = new ArrayList<>();
    private Boolean queueIsRunning = false;
    public static Activity activity;

    private HashMap<String,Object> receivedObjects;

    public static CloudManager getInstance()
    {
        if (instance == null)
        {
            instance = new CloudManager();
        }

        return instance;
    }

    public static void addToQueue(String recordTypeToAddToQueue)
    {
        CloudManager.getInstance().fetchAllDataQueue.add(recordTypeToAddToQueue);
    }

    public static void initQueue()
    {
        CloudManager.getInstance().queueIsRunning = true;
        CloudManager.getInstance().receivedObjects = new HashMap<>();
        loopQueue();
    }

    private static void loopQueue()
    {
        if (CloudManager.getInstance().queueIsRunning)
        {
            if (CloudManager.getInstance().fetchAllDataQueue.size() > 0)
            {
                CloudManager.fetchAllData(CloudManager.getInstance().fetchAllDataQueue.get(0));
            }
            else
            {
                SharedPreferences sharedpreferences = activity.getSharedPreferences("SchedulePreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor sharedPreferencesEditor = sharedpreferences.edit().putLong("lastModificationDate", new java.util.Date().getTime());
                sharedPreferencesEditor.apply();

                CloudManager.getInstance().queueIsRunning = false;

                HashMap<String, Object> returnData = new HashMap<>();
                returnData.put("type", "syncedData");
                returnData.put("objects", CloudManager.getInstance().receivedObjects);

                System.out.println("CM: Notify Obs: " + CloudManager.getInstance().countObservers());
                CloudManager.getInstance().setChanged();

                CloudManager.getInstance().notifyObservers(returnData);
            }
        }
    }

    private static void fetchAllData(String recordType)
    {
        DNKRecordQueryRequest request = DNKRecordQueryRequest.queryWithRecordName(recordType);
        SharedPreferences sharedpreferences = activity.getSharedPreferences("SchedulePreferences", Context.MODE_PRIVATE);

        Long lastModificationLong = sharedpreferences.getLong("lastModificationDate", 0);

        request.addFilter("___modTime", lastModificationLong, "TIMESTAMP", DroidNubeKitConstants.GREATER_THAN_OR_EQUALS);

        DroidNubeKit.fetchRecordsByQuery(request, DroidNubeKitConstants.kDatabaseType.kPublicDatabase, CloudManager.getInstance());
    }

    @Override
    public void success(Object o) {
        if (CloudManager.getInstance().queueIsRunning)
        {
            CloudManager.getInstance().fetchAllDataQueue.remove(0);

            if (((ArrayList<?>) o).size() > 0 && ((ArrayList<?>) o).get(0) != null)
            {
                CloudManager.getInstance().receivedObjects.put(((ArrayList<?>) o).get(0).getClass().getSimpleName(), o);
            }

            loopQueue();
        }
    }

    @Override
    public void failure(Throwable exception) {
        if (CloudManager.getInstance().queueIsRunning)
        {
            loopQueue();
        }
    }
}
