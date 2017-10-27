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
public class InputStreamConcatenator extends InputStream {

    private List<InputStream> streams;
    private int cStrInd = 0;
    private InputStream cStr;

    /**
     *
     */
    public InputStreamConcatenator() {
        streams = new ArrayList<InputStream>();
    }

    public InputStreamConcatenator(List<InputStream> ss) {
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
        if (cStr == null) {
            return -1;
        }

        int c = cStr.read();

        while (c == -1) {
            if (cStrInd == streams.size() - 1) {
                cStr = null;
                break;
            }

            cStr.close();
            cStr = streams.get(++cStrInd);
            c = cStr.read();

        }

        return c;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (cStr == null) {
            return -1;
        }

        int c = cStr.read(b, off, len);

        while (c == -1) {
            if (cStrInd == streams.size() - 1) {
                cStr = null;
                break;
            }
            cStr.close();
            cStr = streams.get(++cStrInd);
            c = cStr.read(b, off, len);

        }

        return c;

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
