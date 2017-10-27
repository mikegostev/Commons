package com.pri.util.stream;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ReaderMultiplexor extends Reader {

    private List<Reader> readers;
    private int current = 0;

    public ReaderMultiplexor() {
        readers = new ArrayList<Reader>(5);
    }

    public void addReader(Reader rd) {
        readers.add(rd);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        while (true) {
            if (current >= readers.size()) {
                return -1;
            }

            int n = readers.get(current).read(cbuf, off, len);

            if (n > 0) {
                return n;
            }

            readers.get(current).close();

            current++;
        }
    }

    public int read() throws IOException {
        while (true) {
            if (current >= readers.size()) {
                return -1;
            }

            int c = readers.get(current).read();

            if (c != -1) {
                return c;
            }

            readers.get(current).close();

            current++;
        }
    }

    @Override
    public void close() throws IOException {
        for (; current < readers.size(); current++) {
            readers.get(current).close();
        }
    }

}
