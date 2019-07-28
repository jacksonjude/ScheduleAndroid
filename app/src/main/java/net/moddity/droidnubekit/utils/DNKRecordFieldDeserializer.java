package net.moddity.droidnubekit.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.moddity.droidnubekit.responsemodels.DNKRecordField;
import net.moddity.droidnubekit.responsemodels.DNKReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaume on 13/7/15.
 */
public class DNKRecordFieldDeserializer implements JsonDeserializer<Map<String, DNKRecordField>> {
    @Override
    public Map<String, DNKRecordField> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, DNKRecordField> field = new HashMap<String, DNKRecordField>();

        JsonObject contents = json.getAsJsonObject();

        for(Map.Entry<String,JsonElement> entry  : contents.entrySet()) {
            DNKRecordField recordField;

            String type = entry.getValue().getAsJsonObject().get("type").getAsString();

            switch (DNKFieldTypes.fromString(type)) {
                case STRING:
                    recordField = new DNKRecordField<String>(DNKFieldTypes.STRING, entry.getValue().getAsJsonObject().get("value").getAsString());
                    break;
                case INT64:
                    recordField = new DNKRecordField<BigInteger>(DNKFieldTypes.INT64, entry.getValue().getAsJsonObject().get("value").getAsBigInteger());
                    break;
                case TIMESTAMP:
                    Timestamp ts = new Timestamp(entry.getValue().getAsJsonObject().get("value").getAsBigInteger().longValue());
                    Date date = new Date(ts.getTime());
                    recordField = new DNKRecordField<Date>(DNKFieldTypes.TIMESTAMP, date);
                    break;
                case REFERENCE:
                    DNKReference singleReference = new DNKReference();
                    JsonObject value = entry.getValue().getAsJsonObject().get("value").getAsJsonObject();
                    singleReference.setRecordName(value.get("recordName").getAsString());
                    singleReference.setAction(value.get("action").getAsString());
                    recordField = new DNKRecordField<DNKReference>(DNKFieldTypes.REFERENCE, singleReference);
                    break;
                case REFERENCE_LIST:
                    JsonArray values = entry.getValue().getAsJsonObject().get("value").getAsJsonArray();

                    List<DNKReference> references = new ArrayList<>();

                    for(int i = 0; i < values.size(); i++) {
                        JsonElement referenceObject = values.get(i);
                        DNKReference reference = new DNKReference();
                        reference.setRecordName(referenceObject.getAsJsonObject().get("recordName").getAsString());
                        //TODO parse zones
                        reference.setAction(referenceObject.getAsJsonObject().get("action").getAsString());

                        references.add(reference);
                    }

                    recordField = new DNKRecordField<List<DNKReference>>(DNKFieldTypes.REFERENCE_LIST, references);
                    break;
                case INT64_LIST:
                    JsonArray intValues = entry.getValue().getAsJsonObject().get("value").getAsJsonArray();
                    List<Integer> integerList = new ArrayList<>();

                    for(int i = 0; i < intValues.size(); i++) {
                        Integer integerElement = (intValues.get(i)).getAsInt();
                        integerList.add(integerElement);
                    }

                    recordField = new DNKRecordField<List<Integer>>(DNKFieldTypes.INT64_LIST, integerList);
                    break;
                case STRING_LIST:
                    JsonArray stringValues = entry.getValue().getAsJsonObject().get("value").getAsJsonArray();

                    List<String> stringList = new ArrayList<>();

                    for(int i = 0; i < stringValues.size(); i++) {
                        String stringElement = (stringValues.get(i)).getAsString();
                        stringList.add(stringElement);
                    }

                    recordField = new DNKRecordField<List<String>>(DNKFieldTypes.STRING_LIST, stringList);

                    break;
                default:
                    recordField = null;
                    break;
            }

            field.put(entry.getKey(), recordField);
        }

        return field;
    }
}
