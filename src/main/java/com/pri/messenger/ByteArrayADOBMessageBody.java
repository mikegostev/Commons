/*
 * Created on 27.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.adob.ADOB;
import com.pri.util.stream.StreamPump;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ByteArrayADOBMessageBody extends ADOBMessageBody {

    private byte[] content;
    private InputStream is;
    private int size = -1;


    public long getContentSize() {
        if (content != null) {
            return content.length;
        }

        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     *
     */
    public ByteArrayADOBMessageBody() {
        super(null, null, null, false);
    }

    public ByteArrayADOBMessageBody(byte[] ba) {
        this(ba, null);
    }

    public ByteArrayADOBMessageBody(byte[] ba, String cType) {
        super(cType, null, null, false);
        setContent(ba);
    }

    public ByteArrayADOBMessageBody(InputStream is) {
        this(is, -1, null);
    }

    public ByteArrayADOBMessageBody(InputStream is, int size, String cType) {
        super(cType, null, null, false);
        setInputStream(is);
        this.size = size;
    }


    public InputStream getInputStream() {
        if (content != null) {
            return new ByteArrayInputStream(content);
        }

        return is;
    }

    public void setInputStream(InputStream is) {
        content = null;
        this.is = is;
    }

    public byte[] getContent() throws IOException {
        if (content != null) {
            return content;
        }

        if (is == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        StreamPump.doPump(is, baos);
        return baos.toByteArray();
    }

    public void setContent(byte[] ba) {
        content = ba;
        is = null;
    }


    public int compareTo(ADOB blob) {
        if (!(blob instanceof ByteArrayADOBMessageBody)) {
            return hashCode() - blob.hashCode();
        }

        if (size != ((ByteArrayADOBMessageBody) blob).size) {
            return size - ((ByteArrayADOBMessageBody) blob).size;
        }

        return hashCode() - blob.hashCode();
    }

}
