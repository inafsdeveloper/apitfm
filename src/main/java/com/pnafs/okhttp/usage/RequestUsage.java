package com.pnafs.okhttp.usage;

public class RequestUsage {
    private boolean _emptyBody;
    private JsonSchemaUsage _schemaUsage;

    public void markEmptyBody() {
        _emptyBody = true;
    }

    public JsonSchemaUsage ensureSchemaUsage() {
        if (_schemaUsage == null) {
            _schemaUsage = new JsonSchemaUsage();
        }
        return _schemaUsage;
    }

    // Getters and Setters

    public boolean is_emptyBody() {
        return _emptyBody;
    }

    public void set_emptyBody(boolean _emptyBody) {
        this._emptyBody = _emptyBody;
    }

    public JsonSchemaUsage get_schemaUsage() {
        return _schemaUsage;
    }

    public void set_schemaUsage(JsonSchemaUsage _schemaUsage) {
        this._schemaUsage = _schemaUsage;
    }
}
