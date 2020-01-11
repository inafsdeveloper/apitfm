package com.pnafs.okhttp.usage;

import java.util.HashMap;
import java.util.Map;

public class PathUsage {
    private Map<String, OperationUsage> _operations = new HashMap<>();

    public OperationUsage ensureOperation(String httpMethod) {
        String cannonicalMethod = httpMethod.toLowerCase();
        return _operations.computeIfAbsent(cannonicalMethod, m -> new OperationUsage());
    }

    // Getters and Setters

    public Map<String, OperationUsage> get_operations() {
        return _operations;
    }

    public void set_operations(Map<String, OperationUsage> _operations) {
        this._operations = _operations;
    }
}
