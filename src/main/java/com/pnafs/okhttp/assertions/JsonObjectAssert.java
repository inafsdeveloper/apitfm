package com.pnafs.okhttp.assertions;

import com.google.common.collect.Sets;
import com.pnafs.okhttp.json.JacksonUtil;
import org.fest.assertions.GenericAssert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ComparisonFailure;

import java.util.*;
import java.util.stream.Collectors;

public class JsonObjectAssert extends GenericAssert<JsonObjectAssert, JSONObject> {

    public JsonObjectAssert(JSONObject actual) {
        super(JsonObjectAssert.class, actual);
    }

    public JsonObjectAssert(String actual) {
        this(new JSONObject((actual)));
    }

    /**
     * Assert that the JSON object has the given key with the given simple value
     *
     * @param key   The key to test
     * @param value The value to test for
     * @return The JsonObjectAssert instance
     * @throws ComparisonFailure If the key is not preset
     */
    public JsonObjectAssert hasKeyValue(String key, Comparable value) {
        hasKey(key);
        Object jsonObjectValue = this.actual.get(key);
        if (!Objects.equals(value, jsonObjectValue)) {
            throw new ComparisonFailure("Expected JSON Object with key '" + key + "' to have value '" + value + "', but was " + jsonObjectValue,
                    "true",
                    "false");
        }
        return this;
    }

    /**
     * Assert that the JSON object has the given key.
     *
     * @param key The key to test for
     * @return The JsonObjectAssert instance
     * @throws ComparisonFailure If the key is not present
     */
    public JsonObjectAssert hasKey(String key) {
        if (!this.actual.has(key)) {
            throw new ComparisonFailure("Expected JSON object to have key '" + key + "'", "true", "false");
        }
        return this;
    }

    /**
     * Asserts that the JSON Object has list of keys
     *
     * @param keys The key to test for
     * @return the JsonObjectAssert instance
     * @throws ComparisonFailure If the keys are not present
     */
    public JsonObjectAssert hasKeys(String[] keys) {
        List<String> missing = new ArrayList<>();
        for (String key : keys) {
            if (!this.actual.has(key)) {
                missing.add(key);
            }
        }

        if (missing.size() > 0) {
            String expectedStr = Arrays.stream(keys).collect(Collectors.joining(","));
            String missingStr = missing.stream().collect(Collectors.joining(","));
            throw new ComparisonFailure("Expected JSON Object to have keys '" + expectedStr + "', but is missing '" + missingStr + "'",
                    "true",
                    "false");
        }
        return this;
    }

    /**
     * Asserts that the JSON object does not have the given key
     *
     * @param key The key to test for
     * @return the JsonObjectAssert instance
     * @throws ComparisonFailure If the key is present
     */
    public JsonObjectAssert doesNotHaveKey(String key) {
        if (this.actual.has(key)) {
            throw new ComparisonFailure("Expected JSON object to NOT have key '" + key + "'", "true", "false");
        }

        return this;
    }

    /**
     * Asserts that the value corresponding to the key is equal to the expected value.
     *
     * @param key           The key to get the actual value
     * @param expectedValue The expected value of the key
     * @return The JsonObjectAssert instance
     */
    public JsonObjectAssert valueEquals(String key, Object expectedValue) {
        Object actualValue = this.actual.get(key);
        if (!Objects.equals(expectedValue, actualValue)) {
            throw new ComparisonFailure("Expected value of key '" + key + "' to be '" + expectedValue + "'",
                    String.valueOf(expectedValue),
                    String.valueOf(actualValue));
        }
        return this;
    }

    @Override
    public JsonObjectAssert isEqualTo(JSONObject expected) {
        List<String> failureMessages = new ArrayList<>();
        compareObjects(expected, this.actual, "$", failureMessages);
        if (!failureMessages.isEmpty()) {
            String actualJson = JacksonUtil.normalizeJson(actual.toString(2));
            System.out.println("");
            System.out.println(">>> Actual json was:");
            System.out.println(actualJson);
            String errorMessage = "\n" + failureMessages.stream().collect(Collectors.joining("\n"));
            throw new ComparisonFailure(errorMessage, JacksonUtil.normalizeJson(expected.toString(2)), actualJson);
        }
        return this;
    }

    public JsonObjectAssert isEqualTo(String expected) {
        return isEqualTo(new JSONObject(expected));
    }

    private void compareObjects(JSONObject expectedObject, JSONObject actualObject, String contextPath, List<String> failureMessages) {
        boolean allowAdditionProperties = false;
        for (String key : expectedObject.keySet()) {
            if ("*".equals(key)) {
                allowAdditionProperties = true;
                continue;
            }

            if (!actualObject.has(key)) {
                addError("Expected the json value to contain the key '" + key + "'", contextPath, failureMessages);
                continue;
            }

            compareValues(expectedObject.get(key), actualObject.get(key), contextPath + "." + key, failureMessages);
        }

        if (!allowAdditionProperties) {
            for (String key : actualObject.keySet()) {
                if (!expectedObject.has(key)) {
                    addError("The actual object contained an unexpected property key '" + key + "'", contextPath, failureMessages);
                }
            }
        }
    }

    private void compareValues(Object expectedValue, Object actualValue, String contextPath, List<String> failureMessages) {
        if ("*".equals(expectedValue)) {
            return;
        }

        if (!compareValueTypes(expectedValue, actualValue, contextPath, failureMessages)) {
            return;
        }

        if (expectedValue instanceof JSONObject) {
            compareObjects((JSONObject) expectedValue, (JSONObject) actualValue, contextPath, failureMessages);
        } else if (expectedValue instanceof JSONArray) {
            compareArrays((JSONArray) expectedValue, (JSONArray) actualValue, contextPath, failureMessages);
        } else {
            if (!Objects.equals(expectedValue, actualValue)) {
                addError("Expected the value to be '" + expectedValue + "' but was '" + actualValue + "'", contextPath, failureMessages);
            }
        }
    }

    private boolean compareValueTypes(Object expectedObject, Object actualObject, String contextPath, List<String> failureMessages) {
        if (expectedObject == actualObject || (expectedObject.getClass() == actualObject.getClass())) {
            return true;
        } else {
            addError("Expected the value to be " + typeName(expectedObject) + " but was " + typeName(actualObject), contextPath, failureMessages);
            return false;
        }
    }

    private void compareArrays(JSONArray expectedList, JSONArray actualList, String contextPath, List<String> failureMessages) {
        if (expectedList.length() != actualList.length()) {
            addError("Expected the array to have " + expectedList.length() + " elements, but had " + actualList.length(), contextPath, failureMessages);
            return;
        }
        for (int i = 0; i < expectedList.length(); i++) {
            compareValues(expectedList.get(i), actualList.get(i), contextPath + "[" + i + "]", failureMessages);
        }
    }

    private String typeName(Object value) {
        if (value instanceof JSONObject) {
            return "an object";
        } else if (value instanceof JSONArray) {
            return "an array";
        } else if (value instanceof String) {
            return "a string";
        } else if (value instanceof Integer) {
            return "an integer";
        } else if (value instanceof Double) {
            return "a double";
        } else if (value instanceof Boolean) {
            return "a boolean";
        } else if (value == null || value == JSONObject.NULL) {
            return "null";
        } else {
            throw new IllegalStateException("Unexpected value type " + value.getClass().getName());
        }

    }

    private void addError(String message, String contextPath, List<String> failureMesages) {
        if ("$".equals(contextPath)) {
            contextPath = "<root>";
        } else if (contextPath.startsWith("$.")) {
            contextPath = contextPath.substring(2);
        }
        failureMesages.add(contextPath + " - " + message);

    }

    public JsonObjectAssert hasErrorDetailMessages(Set<String> errorMessage) {
        Set<String> notFoundExpectedMessages = Sets.newHashSet(errorMessage);
        Set<String> foundMessagesNotExpected = Sets.newHashSet();
        JSONArray details = this.actual.getJSONArray("details");

        if (details == null) {
            fail("Expected response to include details error message. But 'details' was not found in the body");
            return this;
        }

        for (Object obj : details) {
            boolean expectedMessage = false;
            JSONObject detail = (JSONObject) obj;
            String message = (String) detail.get("message");
            for (String notFoundExpectedMessage : notFoundExpectedMessages) {
                if (message.contains(notFoundExpectedMessage)) {
                    notFoundExpectedMessages.remove(notFoundExpectedMessage);
                    expectedMessage = true;
                    break;
                }
            }

            if (!expectedMessage) {
                foundMessagesNotExpected.add(message);
            }
        }

        if (!notFoundExpectedMessages.isEmpty() || !foundMessagesNotExpected.isEmpty()) {
            String actualJson = JacksonUtil.normalizeJson(actual.toString(2));
            System.out.println("");
            System.out.println(">>> Actual json was:");
            System.out.println(actualJson);
            StringBuilder failure = new StringBuilder();
            if (!notFoundExpectedMessages.isEmpty()) {
                failure.append("Expected response to contain all the expected messages, but did not contain; \n")
                        .append(Arrays.toString(notFoundExpectedMessages.toArray()))
                        .append("\n");
            }

            if (!foundMessagesNotExpected.isEmpty()) {
                failure.append("Found messages we did not expect in response: \n")
                        .append(Arrays.toString(foundMessagesNotExpected.toArray()));
            }

            fail(failure.toString());
        }
        return this;
    }
}
