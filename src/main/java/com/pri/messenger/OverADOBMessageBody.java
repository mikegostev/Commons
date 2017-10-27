package com.pri.messenger;

import com.pri.adob.ADOB;
import java.io.IOException;
import java.io.InputStream;

public class OverADOBMessageBody extends ADOBMessageBody {

    private ADOB blob;

    public OverADOBMessageBody(ADOB b) {
        super(b.getContentType(), b.getDisposition(), b.getMetaInfo(), b.isMetaSerialized());
        blob = b;
        setDisposition(b.getDisposition());
        setContentID(b.getContentID());
    }

    public byte[] getContent() throws IOException {
        return blob.getContent();
    }

    public InputStream getInputStream() throws IOException {
        return blob.getInputStream();
    }

    public long getContentSize() {
        return blob.getContentSize();
    }

    public String getContentType() {
        return blob.getContentType();
    }

// public String getContentID()
// {
//  return blob.getContentID();
// }

    public int compareTo(ADOB blob) {
        return hashCode() - blob.hashCode();
    }

    public Object getMetaInfo() {
        return blob.getMetaInfo();
    }

    public boolean isMetaSerialized() {
        return blob.isMetaSerialized();
    }

// public String getMimeDisposition()
// {
//  return blob.getMimeDisposition();
// }

    public void release() {
        blob.release();
    }
}
