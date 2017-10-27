package com.pri.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class HTML2Text extends Reader {

    private static String[] stripTags = new String[]{"SCRIPT", "STYLE"};

    InputStreamReader isr;
    int block = -1;
    int tagPos = -1;
    boolean closing = false;
    int[] cTag = new int[30];
    boolean hasTagName = false;
    boolean prevSpace = false;

    public HTML2Text(InputStream txtS, String charset) throws UnsupportedEncodingException {
        isr = new InputStreamReader(txtS, charset);
    }

    public HTML2Text(InputStream txtS, Charset charset) {
        isr = new InputStreamReader(txtS, charset);
    }

    @Override
    public int read() throws IOException {
        int res = -1;

        do {
            int ch = isr.read();

            if (ch == -1) {
                return -1;
            }

            res = convertChar(ch);

            if (res == ' ' && prevSpace) {
                res = -1;
            }
        } while (res == -1);

        if (res == ' ') {
            prevSpace = true;
        } else {
            prevSpace = false;
        }

        return res;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int cnt = 0;

        do {
            int ch = read();

            if (ch == -1) {
                if (cnt > 0) {
                    return cnt;
                } else {
                    return -1;
                }
            }

            cbuf[off++] = (char) ch;
        } while (++cnt < len);

        return cnt;
    }

    @Override
    public void close() throws IOException {
        isr.close();
    }

    private int convertChar(int ch) {
        if (tagPos == -1) {
            if (ch == '<') {
                tagPos = 0;
                return -1;
            } else if (block == -1) {
                return ch;
            } else {
                return -1;
            }
        }

        if (tagPos == 0 && closing == false && ch == '/') {
            closing = true;
            return -1;
        }

        if (hasTagName) {
            if (ch == '>') {
                tagPos = -1;
                closing = false;
                hasTagName = false;
                return ' ';
            }
            return -1;
        }

        if ((block != -1 && !closing) || (block == -1 && closing)) {
            hasTagName = true;
            return -1;
        }

        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) {
            if (tagPos < cTag.length) {
                cTag[tagPos++] = ch;
            }
        } else {
            String tagName = new String(cTag, 0, tagPos);

            int i;
            for (i = 0; i < stripTags.length; i++) {
                if (stripTags[i].equalsIgnoreCase(tagName)) {
                    break;
                }
            }

            if (i != stripTags.length) {

                if (block == i && closing) {
                    block = -1;
                } else if (block == -1 && !closing) {
                    block = i;
                }
            }

            if (ch == '>') {
                tagPos = -1;
                closing = false;
                hasTagName = false;
                return ' ';
            } else {
                hasTagName = true;
            }


        }

        return -1;
    }

}
