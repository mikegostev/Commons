/*
 * Created on 30.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class InputStreamMultiplexor extends InputStream {

    private static final int BUFFER_SIZE = 2000;
    private static final int CHUNKSIZE_MARGIN = 20;

    private List<InputStream> streams;
    private int cStrInd = 0;
    private InputStream cStr;
    private byte[] buf = new byte[BUFFER_SIZE + CHUNKSIZE_MARGIN];
    private int begPtr = 0, endPtr = 0;

    /**
     *
     */
    public InputStreamMultiplexor() {
        streams = new ArrayList<InputStream>();
    }

    public InputStreamMultiplexor(List<InputStream> ss) {
        streams = ss;

        if (ss.size() > 0) {
            cStr = ss.get(0);
        }
    }

    public void addInputStream(InputStream is) {
        streams.add(is);

        if (streams.size() == 1) {
            cStr = streams.get(0);
        }

    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        if (begPtr >= endPtr) {
            if (!fillBuffer()) {
                return -1;
            }
        }

        return (buf[begPtr++]) & 0xFF;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (begPtr >= endPtr) {
            if (!fillBuffer()) {
                return -1;
            }
        }

        if (len > endPtr - begPtr) {
            len = endPtr - begPtr;
        }

        for (int i = 0; i < len; i++) {
            b[off + i] = buf[begPtr++];
        }

        //System.out.print( new String(b,off,len) );
        return len;

    }

    private boolean fillBuffer() throws IOException {
        boolean endStream = false;

        if (cStr == null) {
            return false;
        }

        int c = cStr.read(buf, CHUNKSIZE_MARGIN, BUFFER_SIZE);

        while (c == -1) {
            if (cStrInd == streams.size() - 1) {
                buf[0] = '\n';
                buf[1] = '0';
                buf[2] = '\n';
//    buf[3]='\n';
//    buf[4]='$';
//    buf[5]='\n';

                begPtr = 0;
                endPtr = 3;

                cStr = null;
                return true;
            }

            endStream = true;
            cStr.close();
            cStr = streams.get(++cStrInd);
            c = cStr.read(buf, CHUNKSIZE_MARGIN, BUFFER_SIZE);
        }

        begPtr = CHUNKSIZE_MARGIN - 1;
        endPtr = CHUNKSIZE_MARGIN + c;

        buf[begPtr--] = '\n';

        int v = c & 0xF;

        while (c != 0) {
            buf[begPtr--] = getHexDigit(v);
            c = c >> 4;
            v = c & 0xF;
        }

        buf[begPtr--] = '\n';

        if (endStream) {
            buf[begPtr--] = '\n';
            buf[begPtr--] = '0';
            buf[begPtr--] = '\n';
        }

        begPtr++;
        return true;
    }

    private byte getHexDigit(int val) {
        if (val > 15) {
            return '?';
        }

        if (val < 10) {
            return (byte) ('0' + val);
        }

        return (byte) ('A' + (val - 10));
    }

    public boolean markSupported() {
        return false;
    }

    public void close() throws IOException {
        Iterator iter = streams.iterator();

        while (iter.hasNext()) {
            ((InputStream) iter.next()).close();
        }
    }
}
