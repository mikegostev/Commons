package com.pri.util;

public class M2codec {

    private static final char escCharLo = '_';
    private static final char escCharHi = '-';

    protected static class Profile {

        char getEscCharLo() {
            return escCharLo;
        }

        char getEscCharHi() {
            return escCharHi;
        }

        boolean isCharLegal(char ch) {
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                return true;
            }

            return false;
        }

    }

    protected static Profile codecProfile = new Profile();


    protected M2codec() {
    }

    public static String encode(String str) {
        return _encode(str, codecProfile);
    }

    public final static boolean isCharLegal(char ch, Profile profile) {
        return profile.isCharLegal(ch) && ch != escCharHi && ch != escCharLo;
    }

    protected static String _encode(String str, Profile profile) {
        int pos = 0;
        char ch = 0;

        while (pos < str.length()) {
            if (!isCharLegal(ch = str.charAt(pos), profile)) {
                break;
            } else {
                pos++;
            }
        }

        if (pos == str.length()) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length() * 3 + 6);

        if (pos > 0) {
            sb.append(str.substring(0, pos));
        }

        do {
            ch = str.charAt(pos);
            if (!isCharLegal(ch, profile)) {
                if (ch < 256) {
                    sb.append(profile.getEscCharLo()).append((char) ('A' + (ch >> 4 & 0x0F)))
                            .append((char) ('A' + (ch & 0x0F)));
                } else {
                    sb.append(profile.getEscCharHi()).append((char) ('A' + (ch >> 12 & 0x0F)))
                            .append((char) ('A' + (ch >> 8 & 0x0F))).append((char) ('A' + (ch >> 4 & 0x0F)))
                            .append((char) ('A' + (ch & 0x0F)));
                }
            } else {
                sb.append(ch);
            }

            pos++;
        } while (pos < str.length());

        return sb.toString();
    }

    public static String decode(String str) {
        return _decode(str, codecProfile);
    }


    public static String _decode(String str, Profile profile) {
        if (str == null) {
            return null;
        }

        int posL = str.indexOf(profile.getEscCharLo());
        int posH = str.indexOf(profile.getEscCharHi());

        if (posL == -1 && posH == -1) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str.length());

        int pos = -1;

        if (posL >= 0) {
            if (posH >= 0) {
                if (posL < posH) {
                    pos = posL;
                } else {
                    pos = posH;
                }
            } else {
                pos = posL;
            }
        } else {
            if (posH >= 0) {
                pos = posH;
            } else {
                return str;
            }
        }

        sb.append(str.substring(0, pos));

        do {
            char ch = str.charAt(pos);

            if (ch == profile.getEscCharLo()) {
                sb.append((char) (((str.charAt(pos + 1) - 'A') << 4) + (str.charAt(pos + 2) - 'A')));
                pos += 2;
            } else if (ch == profile.getEscCharHi()) {
                sb.append((char) (((str.charAt(pos + 1) - 'A') << 12) + ((str.charAt(pos + 2) - 'A') << 8) + (
                        (str.charAt(pos + 3) - 'A') << 4) + (str.charAt(pos + 4) - 'A')));
                pos += 4;
            } else {
                sb.append(ch);
            }

            pos++;
        } while (pos < str.length());

        return sb.toString();
    }


}
