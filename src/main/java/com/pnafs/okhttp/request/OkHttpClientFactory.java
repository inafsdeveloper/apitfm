package com.pnafs.okhttp.request;

import com.pnafs.okhttp.usage.UsageTrackingInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OkHttpClientFactory {
    public static final String REST_TIME_PROPERTY = "rest.timeout";
    public static final String REST_LOG_PROPERTY = "rest.logging";
    public static final String ANALYZE_COVERAGE_PROPERTY = "analyze.coverage";

    private static final OkHttpClient _standardClient = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();

    private static OkHttpClient _defaultClient = createDefaultClient();

    public static OkHttpClient getDefaultClient() {
        return _defaultClient;
    }

    public static OkHttpClient getStandardClient() {
        return _standardClient;
    }

    public static OkHttpClient createDefaultClient() {
        OkHttpClient.Builder builder;
        if (System.getProperty(REST_TIME_PROPERTY) != null) {
            Integer timeout = Integer.valueOf(System.getProperty(REST_TIME_PROPERTY));
            builder = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MINUTES)
                    .readTimeout(timeout, TimeUnit.MINUTES)
                    .writeTimeout(timeout, TimeUnit.MINUTES);
        } else {
            builder = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS);
        }

        if (enableLogging()) {
            builder.addInterceptor(new HttpLoggingInterceptor(new SystemOutLogger()).setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        if ("true".equals(System.getProperty(ANALYZE_COVERAGE_PROPERTY))) {
            builder.addInterceptor(new UsageTrackingInterceptor());
        }
        return builder.build();
    }

    // TODO - Figure Logging
    private static class SystemOutLogger implements HttpLoggingInterceptor.Logger {

        @Override
        public void log(@NotNull String s) {
            System.out.println(s);
        }
    }

    private static boolean enableLogging() {
        String restLoggingProperty = System.getProperty(REST_LOG_PROPERTY);
        return restLoggingProperty == null || "true".equals(restLoggingProperty);
    }
}
