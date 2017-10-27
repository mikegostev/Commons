package com.pri.util;

public interface ProgressListener {

    void dataReceiveSizeKnown(long dataSize);

    void dataReceived(int size);

    void startReceive();

    void endReceive();

    void dataTransferSizeKnown(int size);

    void startTransfer();

    void endTransfer();

    void dataTransfered(int k);
}
