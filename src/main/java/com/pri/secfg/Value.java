package com.pri.secfg;

public class Value {

    private String stringValue;
    private int line;

    public Value() {
    }

    public Value(String varVal, int ln) {
        stringValue = varVal;
        line = ln;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
