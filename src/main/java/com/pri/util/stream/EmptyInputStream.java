package com.pri.util.stream;

import java.io.InputStream;

public class EmptyInputStream extends InputStream {

    public EmptyInputStream() {
        super();
    }

    @Override
    public int read() {
        return -1;
    }

}
