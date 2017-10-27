/*
 * Created on 30.09.2004
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
public class StreamPump {

    final static int BUFSIZE = 2000;

    private InputStream is;
    private OutputStream os;

    /**
     * @param is
     * @param os
     */
/* public StreamPump(InputStream is, OutputStream os)
 {
  this.is = is;
  this.os = os;
 }
*/

    /**
     * @throws IOException
     *
     */

    public int doPump() throws IOException {
        return doPump(is, os);
    }

    public static int doPump(InputStream pis, OutputStream pos) throws IOException {
        return doPump(pis, pos, true, -1);
    }

    public static int doPump(InputStream pis, OutputStream pos, boolean closeOutputStream) throws IOException {
        return doPump(pis, pos, closeOutputStream, -1);
    }

    public static int doPump(InputStream pis, OutputStream pos, boolean closeOutputStream, int sizeLimit)
            throws IOException {
        int n = 0;
        byte[] buf = new byte[BUFSIZE];

        int k = 0;
        while ((k = pis.read(buf)) >= 0) {
            pos.write(buf, 0, k);
            n += k;

            if (sizeLimit > 0 && n > sizeLimit) {
                throw new IOException("Size limit exceeded");
            }
        }

        pis.close();

        if (closeOutputStream) {
            pos.close();
        }

        return n;
    }

    public static int doPump(InputStream pis, OutputStream pos, PumpListener pl) throws IOException {
        return doPump(pis, pos, pl, true);
    }


    public static int doPump(InputStream pis, OutputStream pos, PumpListener pl, boolean closePos) throws IOException {
        int n = 0;
        byte[] buf = new byte[BUFSIZE];

        int k = 0;
        while ((k = pis.read(buf)) >= 0) {
            pos.write(buf, 0, k);
            n += k;
            pl.dataPumped(k);
        }

        pl.endOfStream();

        pis.close();

        if (closePos) {
            pos.close();
        }

        return n;
    }


    public static int doPump(InputStream pis, OutputStream[] poss) throws IOException {
        return doPump(pis, poss, true);
    }

    public static int doPump(InputStream pis, OutputStream[] poss, boolean closePos) throws IOException {
        int n = 0;
        byte[] buf = new byte[BUFSIZE];

        int k = 0;
        while ((k = pis.read(buf)) > 0) {
            for (int i = 0; i < poss.length; i++) {
                poss[i].write(buf, 0, k);
            }

            n += k;
        }

        pis.close();

        if (closePos) {
            for (int i = 0; i < poss.length; i++) {
                poss[i].close();
            }
        }

        return n;
    }

}
