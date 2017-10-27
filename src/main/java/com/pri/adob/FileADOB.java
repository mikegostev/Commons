package com.pri.adob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileADOB extends AbstractADOB {

    private File file;

    private long size = -1;

    private AtomicBoolean temp = new AtomicBoolean(false);

    public FileADOB(File f) {
        this(f, null, false);
    }

    public FileADOB(File f, String ctyp) {
        this(f, ctyp, false);
    }

    public FileADOB(File f, String ctyp, boolean tmp) {
        super(ctyp, null, null, false);
        file = f;

        temp.set(tmp);
    }

    public byte[] getContent() throws IOException {
        byte[] buf = new byte[(int) getContentSize()];

        FileInputStream fis = new FileInputStream(file);

        fis.read(buf);

        fis.close();

        return buf;
    }

    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public long getContentSize() {
        if (size > 0) {
            return size;
        }

        return file.length();
    }

    public boolean setTemporary(boolean tmp) {
        return temp.getAndSet(tmp);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File f) {
        file = f;
        size = -1;
    }

    public String getFileName() {
        return file.getName();
    }

    public void release() {
        if (temp.get() && file != null) {
            file.delete();
            file = null;
        }
    }

    public void finalize() throws Throwable {
        super.finalize();

        if (file != null && temp.get()) {
            file.delete();
            file = null;
        }
    }

    public boolean equals(Object fadb) {
        if (!(fadb instanceof FileADOB)) {
            return false;
        }

        return ((FileADOB) fadb).file.equals(file);
    }
}
