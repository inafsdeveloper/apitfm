package com.pnafs.okhttp.jwt;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Key;
import java.util.Map;

public class TokenGenerator {
    private static final String secret = "abcdefghijklmnop1234567890ABCDEF";

    public static Key getDefaultKey() {
        return new SecretKeySpec(secret.getBytes(Charset.defaultCharset()), "HmacSHA256");
    }

    public static String generateToken(JwsAlgorithm algo, Map<String, Object> header, Map<String, Object> payload, Key key) throws JoseException {
        if (!algo.validSigningKey(key)) {
            throw new IllegalArgumentException(String.format("Invalid key for algorithm '%s' : '%s'", algo.name(), key.getClass().getName()));
        }
        JsonWebSignature jws = new JsonWebSignature();
        jws.setHeader("typ", "JWT");
        header.forEach((k, v) -> jws.setHeader(k, String.valueOf(v)));
        jws.setAlgorithmHeaderValue(algo.name());
        JwtClaims claims = new JwtClaims();
        // set some defaults
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        // set claims
        payload.forEach(claims::setClaim);
        // generate token
        jws.setPayload(claims.toJson());
        jws.setKey(key);
        return jws.getCompactSerialization();
    }
}
