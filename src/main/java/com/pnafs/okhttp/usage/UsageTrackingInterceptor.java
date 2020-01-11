package com.pnafs.okhttp.usage;

import kotlin.text.Charsets;
import okhttp3.*;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import okio.Buffer;

public class UsageTrackingInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        trackRequest(request, response);
        return response;
    }

    private void trackRequest(Request request, Response response) throws IOException {
        String[] urlParts = request.url().toString().split("\\?", 2);
        String url = urlParts[0];
        String queryString = (urlParts.length == 2 ? urlParts[1] : null);

        int firstSlash = url.indexOf('/', 8);
        String path = (firstSlash == -1 ? "/" : url.substring(firstSlash));
        OperationUsage operationUsage = ApiUsage.getInstance().ensurePath(path).ensureOperation(request.method());
        if (queryString != null) {
            String[] queryParamters = queryString.split("&");
            for (String queryParameter : queryParamters) {
                String[] parts = queryParameter.split("=", 2);
                String name = parts[0];
                String value = (parts.length == 2 ? parts[1] : null);
                operationUsage.ensureQueryParameters(name).addValue(value);
            }
        }

        trackRequestBody(request, operationUsage);
        trackReponse(response, operationUsage);
    }

    private void trackRequestBody(Request request, OperationUsage operationUsage) throws IOException {
        RequestUsage requestUsage = operationUsage.ensureRequestUsage();
        RequestBody requestBody = request.body();
        MediaType contentType = (requestBody == null ? null : requestBody.contentType());
        long contentLength = (requestBody == null ? null : requestBody.contentLength());
        if (requestBody != null && contentType != null && contentLength > 0) {
            String baseContentType = contentType.type() + "/" + contentType.subtype();

            if (baseContentType.equals("application/json")) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = contentType.charset();
                String bodyString = buffer.readString(charset == null ? Charsets.UTF_8 : charset);
                requestUsage.ensureSchemaUsage().addJson(bodyString);
            }
        } else {
            requestUsage.markEmptyBody();
        }
    }

    private void trackReponse(Response response, OperationUsage operationUsage) throws IOException {
        int responseCode = response.code();
        ResponseBody responseBody = response.body();
        MediaType contentType = (responseBody == null ? null : responseBody.contentType());
        long contentLength = (responseBody == null ? 0 : responseBody.contentLength());
        if(responseBody != null && contentType != null && contentLength > 0) {
            String baseContentType = contentType.type() + "/" + contentType.subtype();
            ResponseUsage responseUsage = operationUsage.ensureResponseUsage(responseCode);

            if(baseContentType.equals("application/usage")) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.getBuffer();
                Charset charset = contentType.charset();
                String bodyString = buffer.clone().readString(charset == null ? Charsets.UTF_8 : charset);
                responseUsage.ensureSchemaUsage().addJson(bodyString);
            }
        }else {
            operationUsage.ensureResponseUsage(responseCode).markEmptyBody();
        }

    }
}
