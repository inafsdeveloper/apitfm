package com.pnafs.okhttp.request;

import com.pnafs.okhttp.jwt.JwsAlgorithm;
import com.pnafs.okhttp.jwt.TokenGenerator;
import com.pnafs.okhttp.util.RandomEmail;
import okhttp3.*;
import org.jose4j.lang.JoseException;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {
    private static final String INTERNAL_USER = "internal";

    private static class Credentials {
        String username, password;

        Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        Credentials(String username) {
            this(username, "gw");
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    private static class Token {
        Map<String, Object> header, payload;

        Token(Map<String, Object> header, Map<String, Object> payload) {
            this.header = header;
            this.payload = payload;
        }

        public Map<String, Object> getHeader() {
            return header;
        }

        public Map<String, Object> getPayload() {
            return payload;
        }

        String getUserType() {
            return (String) payload.get("type");
        }
    }

    private static class SignatureSpec {
        JwsAlgorithm algorithm;
        Key key;

        SignatureSpec(JwsAlgorithm algorithm, Key key) {
            this.algorithm = algorithm;
            this.key = key;
        }

        public JwsAlgorithm getAlgorithm() {
            return algorithm;
        }

        public Key getKey() {
            return key;
        }

        static SignatureSpec getDefault() {
            return new SignatureSpec(JwsAlgorithm.DEFAULT, TokenGenerator.getDefaultKey());
        }
    }

    private String _baseUrl;
    private Request.Builder _builder = new Request.Builder();
    private Credentials _credentials;
    private Token _token;
    private SignatureSpec _signatureSpec;
    private String _unkownPropertyHandlingHeader = "reject";

    private static final String METADATA_PART_NAME = "metadata";
    private static final String CONTENT_PART_NAME = "content";

    public RequestBuilder withBaseUrl(String baseUrl) {
        this._baseUrl = baseUrl;
        return this;
    }

    public RequestBuilder asUser(String username) {
        _credentials = new Credentials(username);
        return this;
    }

    public RequestBuilder withCredentials(String username, String password) {
        _credentials = new Credentials(username, password);
        return this;
    }

    public RequestBuilder withToken(Map<String, Object> header, Map<String, Object> payload) {
        _token = new Token(header, payload);
        return this;
    }

    public RequestBuilder withToken(Map<String, Object> payload) {
        _token = new Token(new HashMap<>(), payload);
        return this;
    }

    public RequestBuilder withSignatureSpec(JwsAlgorithm algorithm, Key signingKey) {
        _signatureSpec = new SignatureSpec(algorithm, signingKey);
        return this;
    }

    public RequestBuilder withUnknownPropertyHandlingHeader(String headerValue) {
        _unkownPropertyHandlingHeader = headerValue;
        return this;
    }

    public RequestBuilder withChecksumHeader(String value) {
        return withHeader("XY-Checksum", value);
    }

    public RequestBuilder withHeader(String headerName, String headerValue) {
        _builder.header(headerName, headerValue);
        return this;
    }


    public RequestBuilder withPath(String path) {
        if (path.startsWith("http")) {
            _builder.url(path);
        } else {
            if (_baseUrl == null) {
                throw new IllegalStateException("withPath cannnot be called with a relative path unless withBase Url has been called");
            }
            _builder.url(_baseUrl + path);
        }
        return this;
    }

    public ResponseWrapper head(String path) {
        withPath(path);
        _builder.head();
        return makeRequest();
    }

    public ResponseWrapper get(String path) {
        withPath(path);
        _builder.get();
        return makeRequest();
    }

    public ResponseWrapper postEmpty(String path) {
        withPath(path);
        _builder.post(emptyJsonRequestBody());
        return makeRequest();
    }

    public ResponseWrapper postJson(String path, String json) {
        withPath(path);
        _builder.post(jsonRequestBody(json));
        return makeRequest();
    }

    public ResponseWrapper postMultipart(String path, String json, File file) {
        withPath(path);
        MultipartBody reqquestBody = buildMultipartBody(json, file);
        _builder.post(reqquestBody);
        return makeRequest();
    }

    public ResponseWrapper putJson(String path, String json) {
        withPath(path);
        _builder.put(jsonRequestBody(json));
        return makeRequest();
    }

    public ResponseWrapper patchJson(String path, String json) {
        withPath(path);
        _builder.patch(jsonRequestBody(json));
        return makeRequest();
    }

    public ResponseWrapper patchMultipart(String path, String json, File file) {
        withPath(path);
        MultipartBody reqquestBody = buildMultipartBody(json, file);
        _builder.patch(reqquestBody);
        return makeRequest();
    }

    public ResponseWrapper delete(String path) {
        withPath(path);
        _builder.delete();
        return makeRequest();
    }

    private boolean isInternalUser() {
        return _credentials != null || (_token != null &&
                INTERNAL_USER.equals(_token.getUserType()));
    }

    private boolean isExternalUser() {
        return _token != null && !INTERNAL_USER.equals(_token.getUserType());
    }

    private boolean isToken() {
        return _token != null;
    }

    private boolean isCredentials() {
        return _credentials != null;
    }

    private ResponseWrapper makeRequest() {
        if (isToken()) {
            SignatureSpec spec = _signatureSpec;
            if (spec == null) {
                spec = SignatureSpec.getDefault();
            }
            _builder.header("Authorization",
                    encodeBearerToken(spec.algorithm, _token.getHeader(), _token.getPayload(), spec.key));
        } else if (isCredentials()) {
            _builder.header("Authorization",
                    encodeBasicAuth(_credentials.username, _credentials.password));
        } else if (_unkownPropertyHandlingHeader != null) {
            _builder.header("XY-UnknownPropertyHandling", _unkownPropertyHandlingHeader);
        }


        OkHttpClient httpClient = OkHttpClientFactory.getDefaultClient();

        try {
            return ResponseWrapper.wrap(httpClient.newCall(_builder.build()).execute());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }

    public static String encodeBasicAuth(String username, String password) {
        return "Basic" + new String(Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public static String encodeBearerToken(JwsAlgorithm algorithm, Map<String, Object> header, Map<String, Object> payload, Key key) {
        payload.putIfAbsent("iss", "xy");
        payload.putIfAbsent("sub", RandomEmail.get());

        try {
            String token = TokenGenerator.generateToken(algorithm, header, payload, key);
            return "Bearer " + token;
        } catch (JoseException e) {
            throw new RuntimeException("Failed to encode Bearer token: " + e.getMessage(), e);
        }
    }

    public static RequestBody emptyJsonRequestBody() {
        return RequestBody.create(MediaType.parse("application/json"), new byte[0]);
    }

    public static RequestBody jsonRequestBody(String json) {
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    public static RequestBody fileContentRequestBody(File file) {
        String mimeType = _mimeTypesMap.getContentType(file);
        return RequestBody.create(MediaType.parse(mimeType), file);
    }

    private static MultipartBody buildMultipartBody(String json, File file) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if(json != null) {
            bodyBuilder.addFormDataPart(METADATA_PART_NAME, "", jsonRequestBody(json));
        }

        if(file != null) {
            bodyBuilder.addFormDataPart(CONTENT_PART_NAME, file.getName(), fileContentRequestBody(file));
        }

        return bodyBuilder.build();
    }

    private static MimetypesFileTypeMap _mimeTypesMap = initMimeTypesMap();

    private static MimetypesFileTypeMap initMimeTypesMap() {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        mimetypesFileTypeMap.addMimeTypes("application/msword doc");
        mimetypesFileTypeMap.addMimeTypes("application/vnd.openxmlformats-officedocument.wordprocessingml.document docx");
        mimetypesFileTypeMap.addMimeTypes("application/richtext rtx");
        mimetypesFileTypeMap.addMimeTypes("application/pdf pdf");
        mimetypesFileTypeMap.addMimeTypes("text/csv csv");
        mimetypesFileTypeMap.addMimeTypes("text/javascript js");
        mimetypesFileTypeMap.addMimeTypes("application/xml xml");
        return mimetypesFileTypeMap;


    }
}
