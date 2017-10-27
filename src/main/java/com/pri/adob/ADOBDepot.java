package com.pri.adob;

import java.io.IOException;

public interface ADOBDepot {

    Object put(ADOB b) throws IOException;

    boolean containsADOB(Object key);

    ADOB get(Object key) throws IOException;

    long getCreateTime(Object key) throws IOException;

    long getTotalSize();

    long getDataSize();

    long getADOBCount();

    boolean remove(Object key) throws IOException;

    void cleanup() throws IOException;

    void destroy();

}
