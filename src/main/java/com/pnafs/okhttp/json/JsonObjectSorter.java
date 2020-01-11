package com.pnafs.okhttp.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterators;

import java.util.Arrays;

public class JsonObjectSorter {

    public static JsonNode sortTree(JsonNode value) {
        if (value.isObject()) {
            return sortObject((ObjectNode) value);
        } else if (value.isArray()) {
            return sortContainedObject((ArrayNode) value);
        } else {
            return value;
        }
    }

    public static ObjectNode sortObject(ObjectNode objectNode) {
        ObjectNode result = JacksonUtil.OBJECT_MAPPER.createObjectNode();
        String[] sortedKeys = Iterators.toArray(objectNode.fieldNames(), String.class);
        Arrays.sort(sortedKeys);
        for (String key : sortedKeys) {
            result.set(key, sortTree(objectNode.get(key)));
        }

        return result;
    }

    public static ArrayNode sortContainedObject(ArrayNode arrayNode) {
        ArrayNode result = JacksonUtil.OBJECT_MAPPER.createArrayNode();
        for (int i = 0; i < arrayNode.size(); i++) {
            result.add(sortTree(arrayNode.get(i)));
        }

        return result;
    }
}
