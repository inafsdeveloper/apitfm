package com.pnafs.okhttp.json;

import org.json.JSONObject;
import org.json.JSONPointer;

public class JsonElementWrapper extends JsonWrapper {
    protected static final JSONPointer ID_POINTER = new JSONPointer("/attributes/id");
    protected static final JSONPointer CHECKSUM_POINTER = new JSONPointer("/checksum");

    public JsonElementWrapper(JSONObject jsonObject) {
        super(jsonObject);
    }

    public JsonElementWrapper(String json) {
        super(json);
    }

    public String getId() {
        return queryString(getDataObject(), ID_POINTER);
    }

    public String getChecksum() {
        return queryString(getDataObject(), CHECKSUM_POINTER);
    }

    protected JSONObject getDataObject() {
        JSONObject dataObj;
        if (get_jsonObject().has("data")) {
            dataObj = (JSONObject) get_jsonObject().get("data");
        } else {
            dataObj = get_jsonObject();
        }

        return dataObj;
    }
}
