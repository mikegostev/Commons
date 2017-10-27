/*
 * Created on 14.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.util.LightSAXParser;
import org.xml.sax.Attributes;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class LongBody implements AutoParserMessageBody, LightSAXParser {

    private long value = 0;
    private String parseStr = null;

    /**
     *
     */
    public LongBody() {
        super();
    }

    /* (non-Javadoc)
     * @see com.pri.util.XMLable#toXML()
     */
    public String toXML() {
        return "<LONG>" + String.valueOf(value) + "</LONG>";
    }


    /* (non-Javadoc)
     * @see com.pri.messenger.MessageBody#getParser()
     */
    public LightSAXParser getParser() {
        // TODO Auto-generated method stub
        return this;
    }

    /* (non-Javadoc)
     * @see com.pri.util.LightSAXParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml
     * .sax.Attributes)
     */
    @SuppressWarnings("unused")
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName == "LONG") {
            parseStr = "";
        }
    }

    /* (non-Javadoc)
     * @see com.pri.util.LightSAXParser#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
        if (parseStr != null) {
            parseStr += new String(ch, start, length);
        }
    }

    /* (non-Javadoc)
     * @see com.pri.util.LightSAXParser#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unused")
    public void endElement(String namespaceURI, String localName, String qName) {
        if (qName == "LONG") {
            value = Long.parseLong(parseStr);
            parseStr = null;
        }
    }

    public long getLong() {
        return value;
    }

    public void setLong(long l) {
        value = l;
    }

}
