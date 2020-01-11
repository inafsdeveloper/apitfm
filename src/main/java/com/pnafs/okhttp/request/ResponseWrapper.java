package com.pnafs.okhttp.request;

import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class ResponseWrapper {
    private Response _response;
    private boolean _bodyRead = false;
    private String _bodyString;

    public static ResponseWrapper wrap(Response response) {
        return (response == null ? null : new ResponseWrapper(response));
    }

    private ResponseWrapper(Response response) {
        _response = response;
        bodyString();
    }

    public int code() { return _response.code(); }

    public String bodyString() {
        if(_bodyRead) {
            return _bodyString;
        }
        try {
            _bodyString = _response.body().string();
            _bodyRead = true;
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return _bodyString;
    }

    public JSONObject bodyJson() {
        String bodyString = bodyString();
        return (bodyString == null ? null : new JSONObject(bodyString));
    }

    public Response get_response() {
        return _response;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if( obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ResponseWrapper that = (ResponseWrapper) obj;
        return Objects.equals(_response, that._response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_response);
    }
}
