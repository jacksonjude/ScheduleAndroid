package com.jacksonjude.schedule.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.moddity.droidnubekit.annotations.CKField;
import net.moddity.droidnubekit.annotations.RecordName;
import net.moddity.droidnubekit.annotations.RecordType;
import net.moddity.droidnubekit.objects.DNKObject;
import net.moddity.droidnubekit.utils.DNKFieldTypes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jackson on 2/28/18.
 */

@DatabaseTable(tableName = "schedules")
@RecordType("Schedule")
public class Schedule extends DNKObject implements Serializable {

    @RecordName
    @DatabaseField
    public String recordName;

    @CKField(DNKFieldTypes.STRING)
    @DatabaseField(id = true)
    public String scheduleCode;

    @CKField(DNKFieldTypes.STRING_LIST)
    public ArrayList<String> periodTimes;

    @DatabaseField()
    public String periodTimesJSON;

    @CKField(DNKFieldTypes.INT64_LIST)
    public ArrayList<Integer> periodNumbers;

    @DatabaseField()
    public String periodNumbersJSON;
}
