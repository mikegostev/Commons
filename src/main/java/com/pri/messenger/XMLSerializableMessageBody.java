/*
 * Created on 25.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;


import com.pri.util.stream.AsciiDecoderInputStream;
import com.pri.util.stream.AsciiEncoderOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public abstract class XMLSerializableMessageBody implements XMLableMessageBody {

    /* (non-Javadoc)
     * @see com.pri.messenger.MessageBody#toXML()
     */
    public String toXML() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream e = new ObjectOutputStream(new AsciiEncoderOutputStream(baos));
            e.writeObject(this);
            e.close();

            return new String(baos.toByteArray(), "UTF-8");
        } catch (IOException e2) {
        }

        return "";
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.MessageBody#restore(java.lang.String)
     */
    public static MessageBody restore(String encStr) {

        ByteArrayInputStream bais = new ByteArrayInputStream(encStr.getBytes());
        try {
            ObjectInputStream ois = new ObjectInputStream(new AsciiDecoderInputStream(bais));
            return (MessageBody) ois.readObject();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }

        return null;
    }

}
