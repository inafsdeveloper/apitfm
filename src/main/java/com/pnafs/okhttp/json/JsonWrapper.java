package com.pnafs.okhttp.json;

import org.json.JSONObject;
import org.json.JSONPointer;

public class JsonWrapper {
    private final JSONObject _jsonObject;

    public JSONObject get_jsonObject() {
        return _jsonObject;
    }

    public JsonWrapper(JSONObject jsonObject) {
        _jsonObject = jsonObject;
    }

    public JsonWrapper(String json) {
        _jsonObject = new JSONObject(json);
    }

    public String queryString(String jsonPointer) {
        return queryString(_jsonObject, new JSONPointer(jsonPointer));
    }

    public String queryString(JSONPointer jsonPointer) {
        return queryString(_jsonObject, jsonPointer);
    }

    protected String queryString(JSONObject root, JSONPointer jsonPointer) {
        Object found = root.query(jsonPointer);

        if (found == null || found instanceof String) {
            return (String) found;
        } else {
            throw new RuntimeException(String.format("Found type '%s' rather than expected String", found.getClass()));
        }
    }

    public Integer queryInteger(String jsonPointer) {
        return queryInteger(_jsonObject, new JSONPointer(jsonPointer));
    }

    public Integer queryInteger(JSONPointer jsonPointer) {
        return queryInteger(_jsonObject, jsonPointer);
    }

    protected Integer queryInteger(JSONObject root, JSONPointer jsonPointer) {
        Object found = root.query(jsonPointer);

        if (found == null || found instanceof Integer) {
            return (Integer) found;
        } else {
            throw new RuntimeException(String.format("Found type '%s' rather than expected Integer", found.getClass()));
        }
    }

    public Boolean queryBoolean(String jsonPointer) {
        return queryBoolean(_jsonObject, new JSONPointer(jsonPointer));
    }

    public Boolean queryBoolean(JSONPointer jsonPointer) {
        return queryBoolean(_jsonObject, jsonPointer);
    }

    protected Boolean queryBoolean(JSONObject root, JSONPointer jsonPointer) {
        Object found = root.query(jsonPointer);

        if (found == null || found instanceof Boolean) {
            return (Boolean) found;
        } else {
            throw new RuntimeException(String.format("Found type '%s' rather than expected Boolean", found.getClass()));
        }
    }
}
