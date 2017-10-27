package com.pri.util;

public final class M2Pcodec extends M2codec {

    private static Profile codecProfile = new Profile() {
        boolean isCharLegal(char ch) {
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '.') {
                return true;
            }

            return false;
        }
    };

    public static String decode(String str) {
        return _decode(str, codecProfile);
    }

    public static String encode(String str) {
        return _encode(str, codecProfile);
    }

}
