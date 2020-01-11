package com.pnafs.okhttp.assertions;

import com.google.common.collect.Sets;
import com.pnafs.okhttp.request.ResponseWrapper;
import org.fest.assertions.GenericAssert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResponseAssert extends GenericAssert<ResponseAssert, ResponseWrapper> {

    public ResponseAssert(ResponseWrapper actualResponseWrapper) {
        super(ResponseAssert.class, actualResponseWrapper);
    }

    public ResponseAssert hasStatus(int statusCode) {
        isNotNull();
        if (this.actual.code() != statusCode) {
            failIfCustomMessageIsSet();
            String body = this.actual.bodyString();
            if (body != null & body.startsWith("{")) {
                body = new JSONObject(body).toString(2);
            }

            throw fail("Response code did not match: expected " + statusCode + "but got " + this.actual.code()
                    + ". The response body was:\n" + body);
        }
        return this;
    }

    public ResponseAssert hasNoContent() {
        String bodyString = this.actual.bodyString();
        if (!bodyString.equals("")) {
            fail("Expected \"No Content\" response but got " + bodyString);
        }
        return this;
    }

    public ResponseAssert containsHeader(String headerName) {
        isNotNull();
        if (this.actual.get_response().header(headerName) == null) {
            failIfCustomMessageIsSet();
            String actualHeaderNames = this.actual.get_response().headers().names().stream().collect(Collectors.joining(","));
            throw fail("The header \'" + headerName + "\' was not present on the response." +
                    " The actual headers were [" + actualHeaderNames + "]");
        }
        return this;
    }

    public ResponseAssert containsHeader(String headerName, String expectedValue) {
        containsHeader(headerName);
        String value = this.actual.get_response().header(headerName);
        if (value == null || !expectedValue.equals(value)) {
            failIfCustomMessageIsSet();
            throw fail("Expected header \'" + headerName + "\' to have value \'" + expectedValue + "\'," +
                    " but actual value was \'" + value + "\'");
        }

        return this;
    }

    public ResponseAssert doesNotContainHeader(String headerName) {
        isNotNull();
        if (this.actual.get_response().header(headerName) != null) {
            failIfCustomMessageIsSet();
            throw fail("The header \'" + headerName + "\' was present on the response.");
        }
        return this;
    }

    public ResponseAssert isGeneric404Error(String pathInfo) {
        hasStatus(404);

        JSONObject generic404Json = new JSONObject();
        generic404Json.put("errorCode", "api.rest.exception.NotFoundException");
        generic404Json.put("status", 404);
        generic404Json.put("userMessage", "No resource was found at path " + pathInfo);
        return hasResponseJson(generic404Json);
    }

    public ResponseAssert hasUserMessage(String errorMessage) {
        JSONObject errorJson = new JSONObject();
        errorJson.put("*", "*");
        errorJson.put("userMessage", errorMessage);
        return hasResponseJson(errorJson);
    }

    public ResponseAssert hasResponseJson(String expectedJson) {
        JSONObject expectedJsonObject = new JSONObject(expectedJson);
        return hasResponseJson(expectedJsonObject);
    }

    public ResponseAssert hasResponseJson(JSONObject expectedJson) {
        JSONObject responseJson = hasJsonBody();
        new JsonObjectAssert(responseJson).isEqualTo(expectedJson);
        return this;
    }

    public JSONObject hasJsonBody() {
        String responseBody = this.actual.bodyString();
        if (responseBody == null) {
            failIfCustomMessageIsSet();
            throw fail("Expected the response body to not null");
        }

        try {
            return new JSONObject(responseBody);
        } catch (JSONException e) {
            failIfCustomMessageIsSet();
            throw fail("Expected the response to be a json object, but was: \n" + responseBody);
        }
    }

    public ResponseAssert hasRequiredFieldsError(Set<String> requiredFields) {
        isNotNull();
        Set<String> notFound = Sets.newHashSet(requiredFields);
        JSONObject jsonObject = new JSONObject(this.actual.bodyJson());
        JSONArray details = jsonObject.getJSONArray("details");

        if (details == null) {
            failIfCustomMessageIsSet();
            throw fail("Expected response to include details about missing required fields. But 'details' was not found in the body.");
        }

        for (Object detail : details) {
            String message = ((JSONObject) detail).optString("message");
            if (message != null) {
                for (String requiredField : notFound) {
                    if (message.equals("The '" + requiredField + "' property is required")) {
                        notFound.remove(requiredField);
                        break;
                    }
                }
            }
        }

        if (!notFound.isEmpty()) {
            failIfCustomMessageIsSet();
            throw fail("Expected response to complain that the following required fields must be present: \n"
                    + Arrays.toString(notFound.toArray()));
        }
        return this;
    }

    public ResponseAssert hasErrorDetailMessage(Set<String> errorMessages) {
        isNotNull();
        JSONObject jsonObject = new JSONObject(this.actual.bodyJson());
        new JsonObjectAssert(jsonObject).hasErrorDetailMessages(errorMessages);
        return this;
    }

    public ResponseAssert listContainsKeyValue(String listKey, List<Object> keyValuePairs) {
        boolean found = false;
        String key = null;
        String value = null;

        if (this.actual.bodyJson().optQuery(listKey) != null) {
            for (int i = 0; i < keyValuePairs.size(); i += 2) {
                key = (String) keyValuePairs.get(i);
                value = (String) keyValuePairs.get(i + 1);

                JSONArray itemArray = (JSONArray) this.actual.bodyJson().query(listKey);
                for (Object item : itemArray) {
                    JSONObject jItem = (JSONObject) item;

                    if (jItem.optQuery(key) != null) {
                        if (jItem.query(key).equals(value)) {
                            found = true;
                        }
                    }
                }

                if (!found) {
                    fail("Actual response list with key: \"" + listKey + "\" doesn't contain <" + key + "," + value + ">");
                }
                found = false;
            }
        } else {
            fail("Actual response does not have expected key: " + listKey);
        }
        return this;
    }

    public ResponseAssert listContainsKeys(String listKey, List<Object> keys) {
        boolean found = false;
        String key = null;

        if (this.actual.bodyJson().optQuery(listKey) != null) {
            for (int i = 0; i < keys.size(); i++) {
                key = (String) keys.get(i);

                JSONArray itemArray = (JSONArray) this.actual.bodyJson().query(listKey);
                for (Object item : itemArray) {
                    JSONObject jItem = (JSONObject) item;

                    if (jItem.optQuery(key) != null) {
                        found = true;
                    }
                }

                if (!found) {
                    fail("Actual response list with key: \"" + listKey + "\" doesn't contain <" + key + ">");
                }
                found = false;
            }
        } else {
            fail("Actual response does not have expected key: " + listKey);
        }
        return this;
    }
}
