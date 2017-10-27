package com.pri.messenger;

import com.pri.adob.AbstractADOB;

public abstract class ADOBMessageBody extends AbstractADOB implements MessageBody {

    public ADOBMessageBody(String mimeType, String mimeDisposition, Object meta, boolean metaSeri) {
        super(mimeType, mimeDisposition, meta, metaSeri);
    }

}
