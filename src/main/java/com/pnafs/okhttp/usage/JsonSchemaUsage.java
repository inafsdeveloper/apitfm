package com.pnafs.okhttp.usage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonSchemaUsage {
    private Map<String, JsonSchemaPropertyUsage> _propterties = new HashMap<>();

    public void addJson(String json) {
        try {
            JSONObject parsedJson = new JSONObject(json);
            addJson(parsedJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addJson(JSONObject json) {
        for (String key : json.keySet()) {
            ensureProperty(key).addValue(key);
        }
    }

    public JsonSchemaPropertyUsage ensureProperty(String name) {
        return _propterties.computeIfAbsent(name, n -> new JsonSchemaPropertyUsage());
    }

    // Getters and Setters
    public Map<String, JsonSchemaPropertyUsage> get_propterties() {
        return _propterties;
    }

    public void set_propterties(Map<String, JsonSchemaPropertyUsage> _propterties) {
        this._propterties = _propterties;
    }
}
