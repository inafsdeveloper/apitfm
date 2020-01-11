package com.pnafs.okhttp.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JacksonUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String normalizeJson(String json) {
        return normalizeJson(parseJson(json));
    }

    public static String normalizeJson(Path path) {
        return normalizeJson(parseJson(path));
    }

    public static String normalizeJson(JsonNode jsonNode) {
        JsonNode sortedNode = JsonObjectSorter.sortTree(jsonNode);
        return prettyPrintIntelliJ(sortedNode);
    }

    public static JsonNode parseJson(Path path) {
        String contents;
        try {
            contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return parseJson(contents);
    }

    public static JsonNode parseJson(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String prettyPrintIntelliJ(JsonNode object) {
        try {
            return OBJECT_MAPPER.writer(new IntelliJPrettyPrinter()).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
