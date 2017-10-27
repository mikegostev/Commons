package uk.ac.ebi.mg.packedstring;

import java.io.Serializable;

public class SingleBandString extends PackedString implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] bytes;
    private char offset;

    public SingleBandString(String str, char bottom) {
        int len = str.length();

        bytes = new byte[len];

        offset = (char) (bottom + 128);

        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (str.charAt(i) - offset);
        }
    }

    public String toString() {
        char[] chars = new char[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) (bytes[i] + offset);
        }

        return new String(chars);
    }

    @Override
    public int length() {
        return bytes.length;
    }

}
