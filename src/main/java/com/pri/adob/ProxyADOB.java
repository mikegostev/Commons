package com.pri.adob;

import java.io.IOException;
import java.io.InputStream;

public class ProxyADOB extends AbstractADOB {

    private ADOB pxAdob;

    public ProxyADOB(ADOB adb) {
        super(null, null, null, true);
        pxAdob = adb;
        mimeType = null;
    }


    @Override
    public byte[] getContent() throws IOException {
        return pxAdob.getContent();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return pxAdob.getInputStream();
    }

    @Override
    public long getContentSize() {
        return pxAdob.getContentSize();
    }


    public String getContentType() {
        if (mimeType != null) {
            return super.getContentType();
        }

        return pxAdob.getContentType();
    }


    public String getCharset() {
        if (mimeType != null) {
            return super.getCharset();
        }

        return pxAdob.getCharset();
    }

    public String getPrimaryType() {
        if (mimeType != null) {
            return super.getPrimaryType();
        }

        return pxAdob.getPrimaryType();
    }

    public String getSubType() {
        if (mimeType != null) {
            return super.getSubType();
        }

        return pxAdob.getSubType();
    }

    public String getName() {
        if (mimeType != null) {
            return super.getName();
        }

        return pxAdob.getName();
    }

    public String getFileName() {
        if (mimeDisposition != null) {
            return super.getFileName();
        }

        return pxAdob.getFileName();
    }

    public String getTypeParameter(String prm) {
        if (mimeType != null) {
            return super.getTypeParameter(prm);
        }

        return pxAdob.getTypeParameter(prm);
    }

    public String getDispositionParameter(String prm) {
        if (mimeDisposition != null) {
            return super.getDispositionParameter(prm);
        }

        return pxAdob.getDispositionParameter(prm);
    }

    public String getDisposition() {
        if (mimeDisposition != null) {
            return super.getDisposition();
        }

        return pxAdob.getDisposition();
    }

    public String getPosition() {
        if (mimeDisposition != null) {
            return super.getPosition();
        }

        return pxAdob.getPosition();
    }

    public Object getMetaInfo() {
        if (meta != null) {
            return super.getMetaInfo();
        }

        return pxAdob.getMetaInfo();
    }

    public boolean isMetaSerialized() {
        if (meta != null) {
            return super.isMetaSerialized();
        }

        return pxAdob.isMetaSerialized();
    }

    public String getContentID() {
        if (mimeID != null) {
            return super.getContentID();
        }

        return pxAdob.getContentID();
    }


    public void release() {
        pxAdob.release();
    }

}
