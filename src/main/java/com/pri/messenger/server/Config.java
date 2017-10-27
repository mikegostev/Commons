package com.pri.messenger.server;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Config {

    private static long startTime = System.currentTimeMillis();
    private AtomicInteger fileADOBCounter = new AtomicInteger(0);
    private File tmpRoot = new File("/tmp");
    private int maxInlineBodySize = 100000;
    private int maxMessageSize = 2000000;

    public Config() {
    }

    public int getMaxInlineBodySize() {
        return maxInlineBodySize;
    }

    public File getTemporaryFile() {
        return new File(tmpRoot, "adob" + startTime + fileADOBCounter.getAndIncrement());
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public File getTmpRoot() {
        return tmpRoot;
    }

    public void setTmpRoot(File tmpRoot) {
        this.tmpRoot = tmpRoot;
    }

    public void setMaxInlineBodySize(int maxInlineBodySize) {
        this.maxInlineBodySize = maxInlineBodySize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

}
