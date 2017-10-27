package com.pri.adob;

import java.io.IOException;
import java.io.InputStream;

public interface ADOB {

    byte[] getContent() throws IOException;

    InputStream getInputStream() throws IOException;

    String getContentType();

    long getContentSize();

    String getCharset();

    String getPrimaryType();

    String getSubType();

    String getName();

    String getFileName();

    String getTypeParameter(String prm);

    String getDispositionParameter(String prm);

    String getDisposition();

    String getPosition();

    Object getMetaInfo();

    String getContentID();

    boolean isMetaSerialized();

    void release();

}