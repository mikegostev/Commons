/*
 * Created on 03.05.2004
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
public interface LightSAXParser {

    public void startElement(String uri, String localName, String qName, Attributes attributes);

    public void characters(char[] ch, int start, int length);

    public void endElement(String namespaceURI, String localName, String qName);

}
