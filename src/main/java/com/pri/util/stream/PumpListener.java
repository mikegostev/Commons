package com.pri.util.stream;

public interface PumpListener {

    void dataPumped(int k);

    void endOfStream();
}
