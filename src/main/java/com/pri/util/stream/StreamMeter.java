package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class StreamMeter extends InputStream {

    private InputStream stream;
    private int count = 0;

    public StreamMeter(InputStream is) {
        super();
        stream = is;
    }

    @Override
    public int read() throws IOException {
        int ch = stream.read();

        if (ch != -1) {
            count++;
        }

        return ch;
    }

    public int read(byte b[], int off, int len) throws IOException {
        int rd = stream.read(b, off, len);
        count += rd;
        return rd;
    }

    public int read(byte b[]) throws IOException {
        int rd = stream.read(b);
        count += rd;
        return rd;
    }

    public int getStreamSize() {
        return count;
    }

    public void close() throws IOException {
        stream.close();
    }

    public int available() throws IOException {
        return stream.available();
    }

    public boolean equals(Object obj) {
        return stream.equals(obj);
    }

    public int hashCode() {
        return stream.hashCode();
    }

    public void mark(int readlimit) {
        stream.mark(readlimit);
    }

    public boolean markSupported() {
        return stream.markSupported();
    }

    public void reset() throws IOException {
        stream.reset();
    }

    public long skip(long n) throws IOException {
        return stream.skip(n);
    }

    public String toString() {
        return stream.toString();
    }
}
