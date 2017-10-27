/*
 * Created on 07.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class TeeInputStream extends InputStream {

    InputStream is;
    OutputStream os;

    /**
     * @param is
     * @param os
     */
    public TeeInputStream(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;

        try {
            os.write("\n--LOG BEGIN--".getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        int c = is.read();

        if (c != -1) {
            os.write(c);
        } else {
            os.write("--LOG END--\n".getBytes());
        }

        return c;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int c = is.read(b, off, len);

        if (c != -1) {
            os.write(b, off, c);
        } else {
            os.write("\n--LOG END--\n".getBytes());
        }

        return c;

    }

}
