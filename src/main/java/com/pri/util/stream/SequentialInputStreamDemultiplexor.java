package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.StringTokenizer;

public class SequentialInputStreamDemultiplexor implements Iterator<InputStream> {

    private StringTokenizer szst;
    private InputStream mainIs;
// private InputStream;

    public SequentialInputStreamDemultiplexor(InputStream is, String sizes) {
        super();

        szst = new StringTokenizer(sizes.trim(), ",");
        mainIs = is;
    }

    public boolean hasNext() {
        return szst.hasMoreTokens();
    }

    public InputStream next() {
        return new LimitedInputStream(Integer.parseInt(szst.nextToken()));
    }

    public void remove() {
    }

    class LimitedInputStream extends InputStream {

        int len;

        public LimitedInputStream(int l) {
            len = l;
        }

        @Override
        public int read() throws IOException {
            if (len-- > 0) {
                return mainIs.read();
            }

            return -1;
        }

        public int read(byte[] arr, int from, int rlen) throws IOException {
            if (len <= 0) {
                return -1;
            }

            if (rlen > len) {
                rlen = len;
            }

            int r = mainIs.read(arr, from, rlen);

            len -= r;

            return r;
        }


    }
}
