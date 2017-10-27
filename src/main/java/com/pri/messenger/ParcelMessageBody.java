/*
 * Created on 01.04.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import java.io.Serializable;


/**
 * @author Mike
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ParcelMessageBody implements Serializable, SerializableMessageBody {

    private String what;
    private Object obj;

    /**
     * @param obj
     */
    public ParcelMessageBody(String what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    /**
     *
     */
    public ParcelMessageBody() {
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
    public void setObject(Object obj) {
        this.obj = obj;
    }

    /**
     * @return Returns the what.
     */
    public String getWhat() {
        return what;
    }

    /**
     * @param what The what to set.
     */
    public void setWhat(String what) {
        this.what = what;
    }
}
