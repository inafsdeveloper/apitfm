package com.pnafs.okhttp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonMerger {
    public static JSONObject applyJsonDelta(String baseJson, String deltaJson) {
        JSONObject originalJsonObject = new JSONObject(baseJson);
        JSONObject deltaJsonObject = new JSONObject(deltaJson);
        applyJsonDelta(originalJsonObject, deltaJsonObject);
        return originalJsonObject;
    }

    public static void applyJsonDelta(JSONObject baseJson, JSONObject deltaJson) {
        for(String key : deltaJson.keySet()) {
            Object value = deltaJson.get(key);
            if(value == JSONObject.NULL) {
                baseJson.remove(key);
            } else if (value instanceof JSONObject) {
                Object original = baseJson.opt(key);
                if(original == null) {
                    JSONObject newObject = new JSONObject();
                    applyJsonDelta(newObject, (JSONObject) value);
                    baseJson.put(key, newObject);
                } else if (original instanceof JSONObject) {
                    applyJsonDelta((JSONObject) original, (JSONObject) value);
                } else {
                    throw new IllegalArgumentException("Key " + key + " was a "
                            + original.getClass().getSimpleName() +
                            " in the original JSON, but was an object in the delta file.");
                }
            } else if ( value instanceof JSONArray) {
                baseJson.put(key, value);
            } else {
                baseJson.put(key, value);
            }
        }
    }
}
