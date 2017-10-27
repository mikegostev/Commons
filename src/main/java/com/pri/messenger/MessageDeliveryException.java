package com.pri.messenger;


import com.pri.util.LightSAXParser;
import com.pri.util.SimpleTextParser;
import com.pri.util.TextHolder;

public class MessageDeliveryException extends Exception implements AutoParserMessageBody, TextHolder {

    private String text;

    public MessageDeliveryException() {
        super();
    }

    public MessageDeliveryException(String msg) {
        super(msg);
        text = msg;
    }

    public MessageDeliveryException(Throwable t) {
        super(t);
        text = getMessage();
    }

    public void setText(String txt) {
        text = txt;
    }

    public String toXML() {
        return SimpleTextParser.toXML(text);
    }

 /*
  * (non-Javadoc)
  * 
  * @see com.pri.util.MessageBody#startElement(java.lang.String,
  *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
  */

    public String toString() {
        return text;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pri.messenger.MessageBody#getParser()
     */
    public LightSAXParser getParser() {
        return new SimpleTextParser(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pri.util.TextHolder#getText()
     */
    public String getText() {
        return text;
    }
}
