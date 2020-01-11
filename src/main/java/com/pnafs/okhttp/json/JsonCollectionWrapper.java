package com.pnafs.okhttp.json;

import com.google.common.collect.Streams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonCollectionWrapper extends JsonWrapper {
    protected static final JSONPointer DATA_POINTER = new JSONPointer("/data");
    protected static final JSONPointer COUNT_POINTER = new JSONPointer("/count");

    public JsonCollectionWrapper(JSONObject jsonObject) {
        super(jsonObject);
        validateDataArray();
    }

    public JsonCollectionWrapper(String json) {
        super(json);
        validateDataArray();
    }

    /**
     * Returns the raw JSON Array of objects in this collection
     *
     * @return The JSON Array Of Objects
     */
    protected JSONArray getDataArray() {
        return (JSONArray) get_jsonObject().query(DATA_POINTER);
    }

    /**
     * Returns a list of JSON Objects in this collection wrapped by the appropriate JSONElementWrapper.
     *
     * @param wrapperFunc The Function to wrap the Json Object
     * @param <T>         The Type of the JSON Element wrapper
     * @return The list of wrapped JSON elements from this collection
     */
    protected <T extends JsonElementWrapper> List<T> getWrappedDataArray(Function<JSONObject, T> wrapperFunc) {
        return Streams.stream(getDataArray())
                .map(element -> wrapperFunc.apply((JSONObject) element))
                .collect(Collectors.toList());
    }

    public Integer getCount() {
        return queryInteger(COUNT_POINTER);
    }

    private void validateDataArray() {
        if (get_jsonObject().optQuery(DATA_POINTER) == null || !(get_jsonObject().query(DATA_POINTER) instanceof JSONArray)) {
            throw new IllegalStateException("JsonCollectionWrapper may only be constructed for JsonObjects that have a 'data' array element");
        }
    }
}
