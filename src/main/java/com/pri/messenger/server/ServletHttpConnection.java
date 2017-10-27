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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ServletHttpConnection implements HttpConnection {

    HttpServletRequest req;
    HttpServletResponse resp;

    /**
     * @param req
     * @param resp
     */
    public ServletHttpConnection(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
    }

    /**
     * @throws IOException
     *
     */

 /* (non-Javadoc)
  * @see com.pri.shex.backend.HttpConnection#getInputStream()
  */
    public InputStream getInputStream() throws IOException {
        return req.getInputStream();
    }

    /* (non-Javadoc)
     * @see com.pri.shex.backend.HttpConnection#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        return resp.getOutputStream();
    }

    /* (non-Javadoc)
     * @see com.pri.shex.backend.HttpConnection#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String n, String v) {
        resp.setHeader(n, v);
    }

    /* (non-Javadoc)
     * @see com.pri.shex.backend.HttpConnection#getHeaders(java.lang.String)
     */
    public List<String> getHeaders(String h) {
        List<String> l = new ArrayList<String>(3);

        Enumeration hse = req.getHeaders(h);

        while (hse.hasMoreElements()) {
            l.add((String) hse.nextElement());
        }

        return l;
    }

    /* (non-Javadoc)
     * @see com.pri.shex.backend.HttpConnection#getHeader(java.lang.String)
     */
    public String getHeader(String h) {
        return req.getHeader(h);
    }

    /* (non-Javadoc)
    * @see com.pri.shex.backend.HttpConnection#getHeadersIterator()
    */
    public Iterator<StringPair> getHeadersIterator() {
        // TODO Auto-generated method stub
        return new HeadersIterator();
    }

    class HeadersIterator implements Iterator<StringPair> {

        Enumeration hne = req.getHeaderNames();
        StringPair sp = new StringPair();

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return hne.hasMoreElements();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public StringPair next() {
            String nextHdr = (String) hne.nextElement();

            sp.setFirst(nextHdr);
            sp.setSecond(req.getHeader(nextHdr));

            return sp;
        }

    }

}
