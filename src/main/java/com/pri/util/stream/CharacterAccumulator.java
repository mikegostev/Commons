package com.pri.util.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

public class CharacterAccumulator extends Reader {

    final int DEFAULT_MAX_INLINE_SIZE = 100000;

    final static Charset utf16charset = Charset.forName("UTF-16");

    final static int READ_BUFSIZE = 2000;
    private static AtomicInteger accCounter = new AtomicInteger(0);
    private static final String filePrefix = "_characterAccm" + System.currentTimeMillis();

    private Writer fwtr;
    private Reader frdr;
    private File tmpFile;
    private File tmpDir;
    private int maxMemSz = DEFAULT_MAX_INLINE_SIZE;
    private char[] buf = new char[READ_BUFSIZE];
    private int buflimit = 0;
    private int bufoffs = 0;
    private int size = 0;
    private boolean inline = true;
    private boolean closed = false;

    public CharacterAccumulator(File tdir) {
        super();
        tmpDir = tdir;
        tmpFile = new File(tmpDir, filePrefix + accCounter.incrementAndGet());
    }

    public void write(char ch) throws IOException {
        if (!inline) {
            fwtr.write(ch);
            size++;
            return;
        }

        checkBufSize(1);

        if (!inline) {
            fwtr.write(ch);
        } else {
            buf[buflimit++] = ch;
        }

        size++;
        return;
    }

    private void checkBufSize(int len) throws IOException {
        if (buf.length - buflimit < len) {
            int newlen = buf.length * 2;

            if (newlen > maxMemSz) {
                if (fwtr == null) {
                    fwtr = new OutputStreamWriter(new FileOutputStream(tmpFile, true), utf16charset);
                }

                fwtr.write(buf, 0, buflimit);
                inline = false;
                buf = new char[READ_BUFSIZE];
                buflimit = 0;
            } else {
                char newbuf[] = new char[newlen];
                System.arraycopy(buf, 0, newbuf, 0, buflimit);
                buf = newbuf;
            }

        }
    }

    public int appendReader(Reader ir, boolean closeOStr) throws IOException {
        if (closed) {
            throw new IOException("Accumulator already closed");
        }

        int k = 0;
        int len = 0;

        while (true) {
            checkBufSize(READ_BUFSIZE);

            k = ir.read(buf, buflimit, buf.length - buflimit);

            if (k == -1) {
                break;
            }

            size += k;
            len += k;

            if (!inline) {
                fwtr.write(buf, 0, k);
            } else {
                buflimit += k;
            }

        }

        if (closeOStr && fwtr != null) {
            fwtr.close();
            fwtr = null;
        }

        return len;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (!closed) {
            throw new IOException("Accumulator still not closed");
        }

        if (inline) {
            if (buflimit <= bufoffs) {
                return -1;
            }

            if (len > buflimit - bufoffs) {
                len = buflimit - bufoffs;
            }

            System.arraycopy(buf, bufoffs, cbuf, off, len);
            bufoffs += len;
        } else {
            if (frdr == null) {
                frdr = new InputStreamReader(new FileInputStream(tmpFile), utf16charset);
            }

            return frdr.read(cbuf, off, len);
        }

        return len;
    }

    public int read() throws IOException {
        if (!closed) {
            throw new IOException("Accumulator still not closed");
        }

        if (inline) {
            if (buflimit <= bufoffs) {
                return -1;
            }

            return buf[bufoffs++];
        } else {
            if (frdr == null) {
                frdr = new InputStreamReader(new FileInputStream(tmpFile), utf16charset);
            }

            return frdr.read();
        }
    }


    public void close() throws IOException {
        if (fwtr != null) {
            fwtr.close();
        }

        fwtr = null;

        closed = true;
    }

    public void dispose() {
        if (frdr != null) {
            try {
                frdr.close();
            } catch (IOException e) {
            }
        }

        buf = null;
        frdr = null;
        tmpFile.delete();
    }

    public int getMaxInlineSize() {
        return maxMemSz;
    }

    public void setMaxInlineSize(int maxMemSz) {
        this.maxMemSz = maxMemSz;
    }

    public int getSize() {
        return size;
    }

}
