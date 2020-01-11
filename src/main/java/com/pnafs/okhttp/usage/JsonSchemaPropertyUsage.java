package com.pnafs.okhttp.usage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class JsonSchemaPropertyUsage {
    private boolean _nullValue = false;
    private JsonSchemaUsage _objectUsage;
    private Set<Integer> _arraySizes;
    private Set<Object> _scalarValues;

    public JsonSchemaUsage ensureObjectUsage() {
        if (_objectUsage == null) {
            _objectUsage = new JsonSchemaUsage();
        }
        return _objectUsage;
    }

    public Set<Integer> ensureArraySizes() {
        if (_arraySizes == null) {
            _arraySizes = new HashSet<>();
        }

        return _arraySizes;
    }

    public Set<Object> ensureScalarValues() {
        if (_scalarValues == null) {
            _scalarValues = new HashSet<>();
        }
        return _scalarValues;
    }

    public void addValue(Object value) {
        if (value == null) {
            _nullValue = true;
        } else if (value instanceof JSONObject) {
            ensureObjectUsage().addJson((JSONObject) value);
        } else if (value instanceof JSONArray) {
            JSONArray valueArray = (JSONArray) value;
            ensureArraySizes().add(valueArray.length());
            for (Object childValue : valueArray) {
                addValue(value);
            }
        } else if (value == JSONObject.NULL) {
            ensureScalarValues().add(null);
        } else {
            ensureScalarValues().add(value);
        }
    }
    //Getters and Setters

    public boolean is_nullValue() {
        return _nullValue;
    }

    public void set_nullValue(boolean _nullValue) {
        this._nullValue = _nullValue;
    }

    public JsonSchemaUsage get_objectUsage() {
        return _objectUsage;
    }

    public void set_objectUsage(JsonSchemaUsage _objectUsage) {
        this._objectUsage = _objectUsage;
    }

    public Set<Integer> get_arraySizes() {
        return _arraySizes;
    }

    public void set_arraySizes(Set<Integer> _arraySizes) {
        this._arraySizes = _arraySizes;
    }

    public Set<Object> get_scalarValues() {
        return _scalarValues;
    }

    public void set_scalarValues(Set<Object> _scalarValues) {
        this._scalarValues = _scalarValues;
    }
}
