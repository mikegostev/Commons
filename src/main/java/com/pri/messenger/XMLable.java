/*
 * Created on 24.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public interface XMLable {

    public String toXML();

    public boolean restore(String xml);
}
