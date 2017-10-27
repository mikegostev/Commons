/*
 * Created on 05.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger.server;

import com.pri.util.StringPair;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public interface HttpConnection {

    public InputStream getInputStream() throws IOException;

    public OutputStream getOutputStream() throws IOException;

    public void setHeader(String n, String v);

    public List<String> getHeaders(String h);

    public String getHeader(String h);

    public Iterator<StringPair> getHeadersIterator();

}
