package com.pnafs.okhttp.jwt;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public enum JwsAlgorithm {

    HS256(true), HS384(true), HS512(true),
    RS256(false), RS384(false), RS512(false),
    ES256(false), ES384(false), ES512(false),
    PS256(false), PS384(false), PS512(false);
    private boolean symmeteric;

    private JwsAlgorithm(boolean symmetric) {
        this.symmeteric = symmetric;
    }

    public boolean isSymmeteric() {
        return symmeteric;
    }

    public boolean validSigningKey(Key key) {
        return symmeteric ? key instanceof SecretKey : key instanceof PrivateKey;
    }

    public boolean validVerificationKey(Key key) {
        return symmeteric ? key instanceof SecretKey : key instanceof PublicKey;
    }

    public static final JwsAlgorithm DEFAULT = JwsAlgorithm.HS256;
}
