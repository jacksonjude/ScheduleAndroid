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
 * Created by jackson on 3/1/18.
 */

@DatabaseTable(tableName = "weekSchedules")
@RecordType("WeekSchedules")
public class WeekSchedules extends DNKObject implements Serializable {

    @DatabaseField
    @RecordName
    public String recordName;

    @DatabaseField(id = true)
    @CKField(DNKFieldTypes.TIMESTAMP)
    public Long weekStartDate;

    @CKField(DNKFieldTypes.STRING_LIST)
    public ArrayList<String> schedules;

    @DatabaseField()
    public String schedulesJSON;
}