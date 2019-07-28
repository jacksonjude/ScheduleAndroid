package com.jacksonjude.schedule.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.moddity.droidnubekit.annotations.CKField;
import net.moddity.droidnubekit.annotations.RecordName;
import net.moddity.droidnubekit.annotations.RecordType;
import net.moddity.droidnubekit.objects.DNKObject;
import net.moddity.droidnubekit.utils.DNKFieldTypes;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

@DatabaseTable(tableName = "userSchedule")
@RecordType("UserSchedule")
public class UserSchedule extends DNKObject implements Serializable {
    @DatabaseField
    @RecordName
    public String recordName;

    @DatabaseField(id = true)
    @CKField(DNKFieldTypes.STRING)
    public String userID;

    @CKField(DNKFieldTypes.STRING_LIST)
    public ArrayList<String> periodNames;

    @DatabaseField
    @CKField(DNKFieldTypes.INT64_LIST)
    public ArrayList<Integer> freeMods;
}
