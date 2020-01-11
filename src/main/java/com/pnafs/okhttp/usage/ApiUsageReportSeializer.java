package com.pnafs.okhttp.usage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ApiUsageReportSeializer {
    public String serializeUsageReport(ApiUsage usage) {
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(usage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiUsage deserializeUsageReport(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readerFor(ApiUsage.class).readValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
