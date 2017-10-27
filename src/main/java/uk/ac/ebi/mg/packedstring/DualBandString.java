package uk.ac.ebi.mg.packedstring;

import java.io.Serializable;


public class DualBandString extends PackedString implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class PackingImpossibleException extends Exception {

        private static final long serialVersionUID = 1L;
    }

    private byte[] bytes;
    private int offs1;
    private int offs2;
    private int byteoffs;


    public DualBandString(String str, char bottom, char top) throws PackingImpossibleException {
        int len = str.length();

        char subtop, subbottom;

        subtop = top;
        subbottom = bottom;

        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);

            if (ch > subbottom && ch < subtop) {
                if ((ch - bottom) > (top - ch)) {
                    subtop = ch;
                } else {
                    subbottom = ch;
                }
            }
        }

        if (((top - subtop) + (subbottom - bottom)) > 256) {
            throw new PackingImpossibleException();
        }

        byteoffs = subbottom - bottom + 1 - 128;

        offs1 = bottom + 128;
        offs2 = subtop - (subbottom - bottom) + 128 - 1;

        bytes = new byte[len];

        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);

            if (ch <= subbottom) {
                bytes[i] = (byte) ((int) ch - offs1);
            } else {
                bytes[i] = (byte) ((int) ch - offs2);
            }
        }
    }

    public String toString() {
        char[] chars = new char[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] >= byteoffs) {
                chars[i] = (char) (bytes[i] + offs2);
            } else {
                chars[i] = (char) (bytes[i] + offs1);
            }
        }

        return new String(chars);
    }

    @Override
    public int length() {
        return bytes.length;
    }


}
