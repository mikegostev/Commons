package com.pri.adob;

import com.pri.util.stream.StreamPump;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamADOB extends AbstractADOB {

    private long size;
    private InputStream stream;

    public InputStreamADOB(String mimeType, InputStream is, long size) {
        super(mimeType);
        stream = is;
        this.size = size;
    }

    public InputStreamADOB(String type, InputStream is, int sz, Object meta, boolean mtSeri) {
        super(type, null, meta, mtSeri);
        stream = is;
        size = sz;
    }

    @Override
    public byte[] getContent() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamPump.doPump(stream, baos);
        return baos.toByteArray();
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }

    @Override
    public long getContentSize() {
        return size;
    }

}
