package com.pri.adob;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

public class ByteArrayADOB extends AbstractADOB implements Serializable {

    private byte[] bArr;

    public ByteArrayADOB(byte[] arr) {
        this(arr, null, null, null, false);
    }

    public ByteArrayADOB(byte[] arr, String cType) {
        this(arr, cType, null, null, false);
    }

    public ByteArrayADOB(byte[] arr, String cType, String cDisp) {
        this(arr, cType, cDisp, null, false);
    }

    public ByteArrayADOB(byte[] arr, String cType, String cDisp, Object mt, boolean mtSer) {
        super(cType, cDisp, mt, mtSer);

        bArr = arr;
    }

    public byte[] getContent() {
        return bArr;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(bArr);
    }


    public long getContentSize() {
        return bArr.length;
    }


}
