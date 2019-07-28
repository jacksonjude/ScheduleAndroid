package net.moddity.droidnubekit.requests;

/**
 * Created by jackson on 2/28/18.
 */

public class DNKRecordField {
    private Object value;
    private String type;

    public static DNKRecordField createRecordField(Object value, String type)
    {
        DNKRecordField newRecordField = new DNKRecordField();
        newRecordField.setValue(value);
        newRecordField.setType(type);

        return newRecordField;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
