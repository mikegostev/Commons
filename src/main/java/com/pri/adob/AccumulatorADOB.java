package com.pri.adob;

import com.pri.util.stream.ReaderStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

public class AccumulatorADOB extends AbstractADOB {

    final int DEFAULT_MAX_INLINE_SIZE = 100000;

    final static int BUFSIZE = 2000;
    private static AtomicInteger accCounter = new AtomicInteger(0);
    private static final String filePrefix = "_adobAccm" + System.currentTimeMillis();

    private ByteArrayOutputStream baos;
    private FileOutputStream fos;
    private File tmpFile;
    private File tmpDir;
    private int maxMemSz = DEFAULT_MAX_INLINE_SIZE;
    private byte[] buf = new byte[BUFSIZE];
    private int size = 0;
    private boolean inline = true;

    public AccumulatorADOB(String mimeType, File tdir) {
        super(mimeType);
        tmpDir = tdir;
        tmpFile = new File(tmpDir, filePrefix + accCounter.incrementAndGet());
    }

    public AccumulatorADOB(String mimeType, String mimeDisposition, Object meta, boolean metaSeri, File tdir) {
        super(mimeType, mimeDisposition, meta, metaSeri);
        tmpDir = tdir;
        tmpFile = new File(tmpDir, filePrefix + accCounter.incrementAndGet());
    }

    @Override
    public byte[] getContent() throws IOException {
        if (inline) {
            return baos.toByteArray();
        }

        int len = (int) tmpFile.length();
        byte obuf[] = new byte[len];

        FileInputStream fis = new FileInputStream(tmpFile);

        int n = 0;
        int k;
        while (n < len) {
            k = fis.read(buf);
            System.arraycopy(buf, 0, obuf, n, k);
            n += k;
        }

        return obuf;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inline ? new ByteArrayInputStream(baos.toByteArray()) : new FileInputStream(tmpFile);
    }

    @Override
    public long getContentSize() {
        return size;
    }

    public void write(byte b) throws IOException {

        if (inline) {
            baos.write(b);

            if (size > maxMemSz) {
                if (fos == null) {
                    fos = new FileOutputStream(tmpFile);
                }

                fos.write(baos.toByteArray());
                inline = false;
                baos = null;
            }

        } else {
            if (fos == null) {
                fos = new FileOutputStream(tmpFile, true);
            }

            fos.write(b);
        }

        size++;
    }

    public int appendReader(Reader ir, Charset chst, boolean closeOStr) throws IOException {
        return appendStream(new ReaderStream(ir, chst), closeOStr);
    }

    public int appendStream(InputStream is, boolean closeOStr) throws IOException {
        OutputStream os;

        if (inline) {
            if (baos == null) {
                baos = new ByteArrayOutputStream();
            }

            os = baos;
        } else {
            if (fos == null) {
                fos = new FileOutputStream(tmpFile, true);
            }

            os = fos;
        }

        int k = 0;
        int len = 0;
        while ((k = is.read(buf)) >= 0) {
            size += k;
            len += k;

            if (inline && size > maxMemSz) {
                if (fos == null) {
                    fos = new FileOutputStream(tmpFile);
                }

                fos.write(baos.toByteArray());
                os = fos;
                inline = false;
                baos = null;
            }

            os.write(buf, 0, k);
        }

        if (closeOStr && fos != null) {
            fos.close();
            fos = null;
        }

        return len;
    }

    public void close() throws IOException {
        if (fos != null) {
            fos.close();
        }
    }

    public void release() {
        tmpFile.delete();
    }

    public int getMaxInlineSize() {
        return maxMemSz;
    }

    public void setMaxInlineSize(int maxMemSz) {
        this.maxMemSz = maxMemSz;
    }
}
