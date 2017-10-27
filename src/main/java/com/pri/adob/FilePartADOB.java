package com.pri.adob;

import com.pri.util.stream.PartialInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilePartADOB extends AbstractADOB {

    private File file;

    private long size = -1;
    private long offset, length;


    public FilePartADOB(File f, long offs, long len) {
        this(f, null, offs, len);
    }

    public FilePartADOB(File f, String ctyp, long offs, long len) {
        super(ctyp, null, null, false);
        file = f;
        offset = offs;
        length = len;
    }

    public byte[] getContent() throws IOException {
        byte[] buf = new byte[(int) getContentSize()];

        FileInputStream fis = new FileInputStream(file);
        fis.skip(offset);

        fis.read(buf);

        fis.close();

        return buf;
    }

    public InputStream getInputStream() throws IOException {
        InputStream is = new PartialInputStream(new FileInputStream(file), offset, (int) getContentSize());
        return is;
    }

    public long getContentSize() {
        if (size > 0) {
            return size;
        }

        long fl = file.length();
        return size = (fl < offset + length) ? fl - offset : length;
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


    public boolean equals(Object fadb) {
        if (!(fadb instanceof FileADOB)) {
            return false;
        }

        return ((FilePartADOB) fadb).file.equals(file) && ((FilePartADOB) fadb).offset == offset
                && ((FilePartADOB) fadb).length == length;
    }

}
