package com.pnafs.okhttp.util;

public class RandomEmail {
    public static String get() {
        return RandomWordPair.get() + "@example.com";
    }
}
