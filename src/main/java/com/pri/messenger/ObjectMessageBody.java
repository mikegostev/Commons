/*
 * Created on 15.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import java.io.Serializable;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ObjectMessageBody implements Serializable, SerializableMessageBody {

    private static final long serialVersionUID = 333;

    private Serializable obj;

    /**
     * @param obj
     */
    public ObjectMessageBody(Serializable obj) {
        this.obj = obj;
    }

    /**
     *
     */
    public ObjectMessageBody() {
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.MessageBody#toXML()
     */
    public String toXML() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return Returns the obj.
     */
    public Object getObject() {
        return obj;
    }

    /**
     * @param obj The obj to set.
     */
    public void setObject(Serializable obj) {
        this.obj = obj;
    }
}
