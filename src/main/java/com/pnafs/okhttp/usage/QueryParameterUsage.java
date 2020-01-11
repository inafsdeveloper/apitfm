package com.pnafs.okhttp.usage;

import java.util.HashSet;
import java.util.Set;

public class QueryParameterUsage {
    private Set<String> _values = new HashSet<>();

    public void addValue(String value) { _values.add(value); }
    // Getters And Setters
    public Set<String> get_values() {
        return _values;
    }

    public void set_values(Set<String> _values) {
        this._values = _values;
    }
}
