/*
 * Created on 06.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.session;

import javax.servlet.ServletRequest;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class HTTPRequestData implements com.pri.session.RequestData {

    private String addr;

    public HTTPRequestData(ServletRequest rq) {
        addr = rq.getRemoteAddr();
    }

    /**
     * @return Returns the addr.
     */
    public String getAddr() {
        return addr;
    }

}
