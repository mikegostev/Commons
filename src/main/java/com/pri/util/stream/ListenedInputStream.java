package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class ListenedInputStream extends InputStream {

    private InputStream liStream;
    private PumpListener pumpLsnr;
    long size;
    long readCount = 0;

    public ListenedInputStream(InputStream is, PumpListener pl) {
        super();
        liStream = is;
        pumpLsnr = pl;
        size = Long.MAX_VALUE;
    }

    public ListenedInputStream(InputStream is, PumpListener pl, long sz) {
        super();
        liStream = is;
        pumpLsnr = pl;

        if (sz < 0) {
            size = Long.MAX_VALUE;
        } else {
            size = sz;
        }
    }

    @Override
    public int read() throws IOException {
        if (readCount >= size) {
            return -1;
        }

        try {
            int n = liStream.read();

            if (n != -1) {
                pumpLsnr.dataPumped(1);
                readCount++;

                if (readCount == size) {
                    pumpLsnr.endOfStream();
                }
            } else {
                pumpLsnr.endOfStream();
            }

            return n;
        } catch (IOException e) {
            pumpLsnr.endOfStream();
            throw e;
        }
    }

    public int available() throws IOException {
        if (size < Long.MAX_VALUE) {
            return (int) (size - readCount);
        }

        return liStream.available();
    }

    public void close() throws IOException {
        liStream.close();
    }

    public boolean equals(Object arg0) {
        return liStream.equals(arg0);
    }

    public int hashCode() {
        return liStream.hashCode();
    }

    public void mark(int arg0) {
        liStream.mark(arg0);
    }

    public boolean markSupported() {
        return liStream.markSupported();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        if (readCount >= size) {
            return -1;
        }

        int rem = (int) (size - readCount);
        if (size < Long.MAX_VALUE && len > rem) {
            len = rem;
        }

        int n = liStream.read(buf, off, len);

        if (n > 0) {
            pumpLsnr.dataPumped(n);
            readCount += n;

            if (readCount == size) {
                pumpLsnr.endOfStream();
            }
        } else if (n == -1 || len > 0) {
            pumpLsnr.endOfStream();
        }

        return n;
    }

    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    public void reset() throws IOException {
        liStream.reset();
    }

    public long skip(long toSkip) throws IOException {
        long n = liStream.skip(toSkip);

        if (n > 0) {
            readCount += n;

            if (readCount >= size) {
                pumpLsnr.endOfStream();
            }
        } else {
            pumpLsnr.endOfStream();
        }

        return n;
    }

    public String toString() {
        return liStream.toString();
    }

}
