/*
 * Created on 27.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.util.collection.EmptyIterator;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class TransferUnit {

    private String contentType;
    private Map<String, String> properties;
    private InputStream contentIS;
    private int contentSize = -1;
    private String method = "POST";

    /**
     *
     */
    public TransferUnit() {
    }

    public String getContentType() {
        return contentType;
    }

    public String getProperty(String name) {
        if (properties == null) {
            return null;
        }

        return properties.get(name);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public InputStream getInputStream() {
        return contentIS;
    }
 
/* public byte[] getContent()
 {
  ByteArrayOutputStream baos = new ByteArrayOutputStream( contentIS );
  return content;
 }
*/

    /**
     * @param content The content to set.
     */
    public void setContentInputStream(InputStream is) {
        this.contentIS = is;
    }

    /**
     * @param contentType The contentType to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param properties The properties to set.
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void setProperty(String name, String value) {
        if (properties == null) {
            properties = new TreeMap<String, String>();
        }

        properties.put(name, value);
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        if (properties == null) {
            return EmptyIterator.<Map.Entry<String, String>>getInstance();
        }

        return properties.entrySet().iterator();
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String mth) {
        method = mth;
    }
}

