/*
 * Created on 26.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public abstract class BeanMessageBody implements XMLableMessageBody {


    /* (non-Javadoc)
     * @see com.pri.messenger.MessageBody#toXML()
     */
    public String toXML() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(baos);
        e.writeObject(this);
        e.close();

        try {
            return "<![CDATA[" + new String(baos.toByteArray(), "UTF-8") + "]]>";
        } catch (UnsupportedEncodingException e1) {
        }

        return "";
    }

}
