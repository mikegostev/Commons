package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;

;

public class AsciiDecoderInputStream extends java.io.InputStream {

    private InputStream overStream;
    private byte[] decBytes;
    private static final int MAXDECBYTES = 200;
    private static final byte ASCIIOFFSET = 65;

    private boolean hasOdd;
    private byte odd;

 /* (non-Javadoc)
  * @see java.io.InputStream#read()
  */

    public AsciiDecoderInputStream(InputStream is) {
        overStream = is;
        decBytes = new byte[MAXDECBYTES];
        hasOdd = false;
    }

    public int read() throws IOException {
        if (hasOdd) {
            int nextB = overStream.read();

            if (nextB == -1) {
                throw new IOException("Unexpexted end of stream");
            }

            hasOdd = false;
            return (odd - ASCIIOFFSET) | (((nextB - ASCIIOFFSET) << 4) & 0xF0);

        }
        overStream.read(decBytes, 0, 2);
        return (decBytes[0] - ASCIIOFFSET) | ((decBytes[1] - ASCIIOFFSET) << 4);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int oPtr = off;

        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int readLen;

        if (len * 2 > MAXDECBYTES) {
            readLen = MAXDECBYTES;
        } else {
            readLen = len * 2;
        }

        while (oPtr - off < len) {
            int iPtr = 0;

            if ((len - (oPtr - off)) * 2 > MAXDECBYTES) {
                readLen = MAXDECBYTES;
            } else {
                readLen = (len - (oPtr - off)) * 2;
            }

            int l = overStream.read(decBytes, 0, readLen);

            if (l == -1 && oPtr == off) {
                return -1;
            }

            if (l <= 0) {
                return oPtr - off;
            }

            if (hasOdd) {
                b[oPtr++] = (byte) ((odd - ASCIIOFFSET) | ((decBytes[iPtr++] - ASCIIOFFSET) << 4));
                hasOdd = false;
            }

            while (iPtr < l) {
                if (iPtr + 1 == l) {
                    odd = decBytes[iPtr];
                    hasOdd = true;
                    break;
                }

                byte first = (byte) (decBytes[iPtr++] - ASCIIOFFSET);
                b[oPtr++] = (byte) (first | ((decBytes[iPtr++] - ASCIIOFFSET) << 4));

            }

        }

        return oPtr - off;

    }
}