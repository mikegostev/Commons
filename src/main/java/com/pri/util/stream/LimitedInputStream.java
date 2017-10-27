package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {

    private int limit;
    private InputStream mainIs;

    public LimitedInputStream(InputStream is, int lim) {
        limit = lim;
        mainIs = is;
    }

    @Override
    public int read() throws IOException {
        if (limit-- > 0) {
            return mainIs.read();
        }

        throw new LimitExeededException();
    }

    public long skip(long toskip) throws IOException {
        if (toskip > limit) {
            throw new LimitExeededException();
        }

        long n = mainIs.skip(toskip);
        limit -= n;
        return n;
    }

    public int read(byte[] arr) throws IOException {
        if (limit <= 0) {
            throw new LimitExeededException();
        }

        int r = mainIs.read(arr);

        limit -= r;

        if (limit < 0) {
            throw new LimitExeededException();
        }

        return r;
    }

    public int read(byte[] arr, int from, int rlen) throws IOException {
        if (limit <= 0) {
            throw new LimitExeededException();
        }

        int r = mainIs.read(arr, from, rlen);

        limit -= r;

        if (limit < 0) {
            throw new LimitExeededException();
        }

        return r;
    }

    public static class LimitExeededException extends IOException {

        public LimitExeededException() {
            super();
        }

        public LimitExeededException(String s) {
            super(s);
        }
    }
}
