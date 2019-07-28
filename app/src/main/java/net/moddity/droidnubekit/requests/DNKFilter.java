package net.moddity.droidnubekit.requests;

/**
 * Created by jackson on 2/28/18.
 */

public class DNKFilter {
    private String fieldName;
    private DNKRecordField fieldValue;
    private String comparator;

    public static DNKFilter createFilter(String fieldName, DNKRecordField fieldValue, String comparator)
    {
        DNKFilter newFilter = new DNKFilter();
        newFilter.setFieldName(fieldName);
        newFilter.setFieldValue(fieldValue);
        newFilter.setComparator(comparator);

        return newFilter;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public void setFieldValue(DNKRecordField fieldValue)
    {
        this.fieldValue = fieldValue;
    }

    public void setComparator(String comparator)
    {
        this.comparator = comparator;
    }
}
