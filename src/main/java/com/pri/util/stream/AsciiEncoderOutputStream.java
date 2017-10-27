/*
 * Created on 25.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.stream;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class AsciiEncoderOutputStream extends OutputStream {

    OutputStream underStream;
    byte[] encBytes;
    static final int MAXENCBYTES = 200;
    static final byte ASCIIOFFSET = 65;

    /**
     *
     */
    public AsciiEncoderOutputStream(OutputStream us) {
        super();
        underStream = us;
        encBytes = new byte[MAXENCBYTES];
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    public void write(int byt) throws IOException {
        encBytes[0] = (byte) ((byt & 0xF) + ASCIIOFFSET);
        encBytes[1] = (byte) (((byt >> 4) & 0xF) + ASCIIOFFSET);

        underStream.write(encBytes, 0, 2);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        int iPtr = off;

        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        int nEnc;

        while (iPtr - off < len) {
            int oPtr = 0;

            if ((len - (iPtr - off)) * 2 > MAXENCBYTES) {
                nEnc = MAXENCBYTES;
            } else {
                nEnc = (len - (iPtr - off)) * 2;
            }

            while (oPtr < nEnc) {
                encBytes[oPtr++] = (byte) ((b[iPtr] & 0xF) + ASCIIOFFSET);
                encBytes[oPtr++] = (byte) (((b[iPtr] >> 4) & 0xF) + ASCIIOFFSET);
                iPtr++;
            }

            underStream.write(encBytes, 0, oPtr);

        }
    }

    public void close() throws IOException {
        underStream.close();
        encBytes = null;
    }

    public void flush() throws IOException {
        underStream.flush();
    }

}
