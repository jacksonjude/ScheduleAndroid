package net.moddity.droidnubekit.requests;

import java.util.ArrayList;

/**
 * Created by jaume on 12/6/15.
 */
public class DNKQuery {

    private String recordType;
    //TODO filterBy
    //TODO sortBy

    private ArrayList<DNKFilter> filterBy;


    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public void addFilter(DNKFilter filter)
    {
        if (filterBy == null)
        {
            filterBy = new ArrayList<DNKFilter>();
        }
        filterBy.add(filter);
    }
}
