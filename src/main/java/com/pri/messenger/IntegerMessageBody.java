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
public class IntegerMessageBody implements Serializable, SerializableMessageBody {

    private int val;

    /**
     * @param obj
     */
    public IntegerMessageBody(int v) {
        this.val = v;
    }

    /**
     *
     */
    public IntegerMessageBody() {
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.MessageBody#toXML()
     */
    public String toXML() {
        // TODO Auto-generated method stub
        return null;
    }

    public int intValue() {
        return val;
    }

    /**
     * @param obj The obj to set.
     */
    public void setValue(int v) {
        this.val = v;
    }
}
