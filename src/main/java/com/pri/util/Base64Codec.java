package com.pri.util;

import java.util.Arrays;

public class Base64Codec {

    private static final char[] xTbl = new char[]{'-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private Base64Codec() {
    }

    public static String encode(byte[] toEnc) {
        StringBuffer sb = new StringBuffer(toEnc.length * 4 / 3 + 4);

        int i = 0;

        while (i + 2 < toEnc.length) {
            sb.append(xTbl[toEnc[i] & 0x3F]);
            sb.append(xTbl[((toEnc[i] >>> 2) & 0x30) | (toEnc[i + 1] & 0x0F)]);
            sb.append(xTbl[((toEnc[i + 1] >>> 2) & 0x3C) | (toEnc[i + 2] & 0x03)]);
            sb.append(xTbl[(toEnc[i + 2] >>> 2) & 0x3F]);

            i += 3;
        }

        if (i < toEnc.length) {
            sb.append(xTbl[toEnc[i++] & 0x3F]);

            if (i < toEnc.length) {
                sb.append(xTbl[((toEnc[i - 1] >>> 2) & 0x30) | (toEnc[i] & 0x0F)]);
                sb.append(xTbl[(toEnc[i] >>> 2) & 0x3C]);
            } else {
                sb.append(xTbl[(toEnc[i - 1] >>> 2) & 0x30]);
            }
        }

        return sb.toString();
    }

    public static byte[] decode(String toDec) throws InvalidParameterException {
        int strLen = toDec.length();
        int len = strLen / 4 * 3;
        int rem = strLen % 4;

        if (rem > 0) {
            if (rem == 1) {
                throw new InvalidParameterException("Wrong length: " + strLen, new Integer(strLen));
            }

            len += rem - 1;
        }

        byte[] res = new byte[len];

        int i = 0;
        int j = 0;
        while (i + 3 < strLen) {
            int v1 = Arrays.binarySearch(xTbl, toDec.charAt(i));
            if (v1 < 0) {
                throw new InvalidParameterException("Invalid character: " + toDec.charAt(i), new Integer(i));
            }

            res[j] = (byte) v1;

            int v2 = Arrays.binarySearch(xTbl, toDec.charAt(i + 1));
            if (v2 < 0) {
                throw new InvalidParameterException("Invalid character: " + toDec.charAt(i + 1), new Integer(i + 1));
            }

            res[j] |= (v2 & 0x30) << 2;
            res[j + 1] = (byte) (v2 & 0x0F);

            int v3 = Arrays.binarySearch(xTbl, toDec.charAt(i + 2));
            if (v3 < 0) {
                throw new InvalidParameterException("Invalid character: " + toDec.charAt(i + 2), new Integer(i + 2));
            }

            res[j + 1] |= (v3 << 2) & 0xF0;
            res[j + 2] = (byte) (v3 & 0x03);

            int v4 = Arrays.binarySearch(xTbl, toDec.charAt(i + 3));
            if (v4 < 0) {
                throw new InvalidParameterException("Invalid character: " + toDec.charAt(i + 3), new Integer(i + 3));
            }

            res[j + 2] |= v4 << 2;

            j += 3;
            i += 4;
        }

        if (i < strLen) {
            int v1 = Arrays.binarySearch(xTbl, toDec.charAt(i));
            if (v1 < 0) {
                throw new InvalidParameterException("Invalid character: " + toDec.charAt(i), new Integer(i));
            }

            res[j] = (byte) v1;

            int v2 = Arrays.binarySearch(xTbl, toDec.charAt(i + 1));
            if (v2 < 0) {
                throw new InvalidParameterException("Invalid character: " + toDec.charAt(i + 1), new Integer(i + 1));
            }

            res[j] |= (v2 & 0x30) << 2;

            i += 2;

            if (i < strLen) {
                res[j + 1] = (byte) (v2 & 0x0F);

                int v3 = Arrays.binarySearch(xTbl, toDec.charAt(i));
                if (v3 < 0) {
                    throw new InvalidParameterException("Invalid character: " + toDec.charAt(i), new Integer(i + 2));
                }

                res[j + 1] |= (v3 << 2) & 0xF0;

            }


        }

        return res;
    }

}
