/*
 * Created on 04.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;

import org.xml.sax.Attributes;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class SimpleTextParser implements LightSAXParser {

    private TextHolder textHolder;
    private String text;
    private boolean doExtract;

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     *
     */
    public SimpleTextParser(TextHolder th) {
        textHolder = th;
    }

    /* (non-Javadoc)
     * @see com.pri.util.LightSAXParser#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml
     * .sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("TEXT")) {
            doExtract = true;
        }

        text = "";
    }

    public void characters(char[] ch, int start, int length) {
        if (doExtract) {
            text += new String(ch, start, length);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        if (qName.equalsIgnoreCase("TEXT")) {
            doExtract = false;
        }

        textHolder.setText(text);
    }


    public static String toXML(String text) {
        return "<TEXT>" + XMLHelper.quotByEntity(text) + "</TEXT>";

    }

}
