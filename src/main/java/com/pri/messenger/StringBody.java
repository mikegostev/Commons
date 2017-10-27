package com.pri.messenger;

import com.pri.util.LightSAXParser;
import com.pri.util.SimpleTextParser;
import com.pri.util.TextHolder;

public class StringBody implements AutoParserMessageBody, TextHolder {

    private String text;

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    public StringBody() {
        this.text = "";
    }

    public StringBody(String text) {
        if (text == null) {
            this.text = "";
        } else {
            this.text = text;
        }
    }

    public String toXML() {
        return SimpleTextParser.toXML(text);
    }

    public String toString() {
        return text;
    }

    public LightSAXParser getParser() {
        return new SimpleTextParser(this);
    }
}
