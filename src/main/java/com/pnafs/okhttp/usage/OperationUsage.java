package com.pnafs.okhttp.usage;

import java.util.HashMap;
import java.util.Map;

public class OperationUsage {
    private Map<String, QueryParameterUsage> _queryParameters = new HashMap<>();
    private RequestUsage _requestUsage = null;
    private Map<Integer, ResponseUsage> _responseUsage = new HashMap<>();

    public QueryParameterUsage ensureQueryParameters(String name) {
        return _queryParameters.computeIfAbsent(name, n -> new QueryParameterUsage());
    }

    public RequestUsage ensureRequestUsage() {
        if (_requestUsage == null) {
            _requestUsage = new RequestUsage();
        }
        return _requestUsage;
    }

    public ResponseUsage ensureResponseUsage(Integer responseCode) {
        return _responseUsage.computeIfAbsent(responseCode, rc -> new ResponseUsage());
    }

    // Getters and Setters


    public Map<String, QueryParameterUsage> get_queryParameters() {
        return _queryParameters;
    }

    public void set_queryParameters(Map<String, QueryParameterUsage> _queryParameters) {
        this._queryParameters = _queryParameters;
    }

    public RequestUsage get_requestUsage() {
        return _requestUsage;
    }

    public void set_requestUsage(RequestUsage _requestUsage) {
        this._requestUsage = _requestUsage;
    }

    public Map<Integer, ResponseUsage> get_responseUsage() {
        return _responseUsage;
    }

    public void set_responseUsage(Map<Integer, ResponseUsage> _responseUsage) {
        this._responseUsage = _responseUsage;
    }
}
