package com.pnafs.okhttp.usage;

import java.util.HashMap;
import java.util.Map;

public class ApiUsage {
    private static ApiUsage _instance = new ApiUsage();

    public static ApiUsage getInstance() {
        return _instance;
    }

    private Map<String, PathUsage> _paths = new HashMap<>();

    public PathUsage ensurePath(String path) {
        return _paths.computeIfAbsent(path, p -> new PathUsage());
    }

    // Getter and Setters
    public Map<String, PathUsage> get_paths() {
        return _paths;
    }

    public void set_paths(Map<String, PathUsage> _paths) {
        this._paths = _paths;
    }
}
