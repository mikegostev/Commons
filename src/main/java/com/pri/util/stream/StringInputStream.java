package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class StringInputStream extends InputStream {

    private byte[] strBytes;
    private int pos = 0;

    public StringInputStream(String ss) {
        try {
            strBytes = ss.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
        }
    }

    @SuppressWarnings("unused")
    @Override
    public int read() throws IOException {
        if (pos >= strBytes.length) {
            return -1;
        }

        return strBytes[pos++];
    }

}
