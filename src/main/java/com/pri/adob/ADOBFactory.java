package com.pri.adob;

import java.io.IOException;
import java.io.InputStream;

public interface ADOBFactory {

    ADOB createADOB(String type, int size, String contID, String disp, InputStream is, Object meta, boolean mtSeri,
            boolean canDelay) throws IOException;
}
