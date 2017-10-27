package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;


public class ReaderStream extends InputStream {

    static final int BUFSIZE = 2000;

    private Reader reader;
    private Charset cs;
    private CharBuffer cbuf;
    private ByteBuffer bbuf;

    public ReaderStream(Reader ir, Charset chst) {
        super();
        reader = ir;
        cs = chst;
        cbuf = CharBuffer.allocate(BUFSIZE);
    }

    @Override
    public int read() throws IOException {
        if (bbuf == null) {
            if (!fillBuffer()) {
                return -1;
            }
        }

        byte byt = bbuf.get();

        if (!bbuf.hasRemaining()) {
            bbuf = null;
        }

        return byt;
    }

    public int read(byte[] barr, int offs, int len) throws IOException {
        if (bbuf == null) {
            if (!fillBuffer()) {
                return -1;
            }
        }

        int n = len > bbuf.remaining() ? bbuf.remaining() : len;
        bbuf.get(barr, offs, n);

        if (!bbuf.hasRemaining()) {
            bbuf = null;
        }

        return n;
    }

    private boolean fillBuffer() throws IOException {
        cbuf.clear();
        int n;
        if ((n = reader.read(cbuf)) == -1) {
            return false;
        }

        cbuf.limit(n);
        cbuf.rewind();

        bbuf = cs.encode(cbuf);

        return true;
    }

}
