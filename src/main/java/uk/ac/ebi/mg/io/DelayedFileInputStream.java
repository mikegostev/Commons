package uk.ac.ebi.mg.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelayedFileInputStream extends InputStream {

    private File file;
    private InputStream is;

    public DelayedFileInputStream(File f) {
        file = f;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (is != null) {
            is.close();
        }

        is = null;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (is == null) {
            is = new FileInputStream(file);
        }

        int rd = is.read(b, off, len);

        if (rd < 0) {
            is.close();
            is = null;
        }

        return rd;
    }

    @Override
    public int read() throws IOException {
        if (is == null) {
            is = new FileInputStream(file);
        }

        int rd = is.read();

        if (rd < 0) {
            is.close();
            is = null;
        }

        return rd;
    }

}
