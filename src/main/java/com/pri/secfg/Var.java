package com.pri.secfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Var {

    private String name;
    Value value;
    List<Value> values;

    public Var(String varName, String varVal, int ln) {
        name = varName;
        value = new Value(varVal, ln);
    }

    public void addValue(String varVal, int ln) {
        if (values == null) {
            values = new ArrayList<Value>(5);
            values.add(value);
        }

        Value nv = new Value(varVal, ln);

        values.add(nv);
        value = nv;
    }

    public String getName() {
        return name;
    }

    public Value getValue() {
        return value;
    }

    public String getStringValue() {
        if (value != null) {
            return value.getStringValue();
        }

        return null;
    }

    public List<Value> getValues() {
        if (values == null) {
            return Collections.singletonList(value);
        }

        return values;
    }

    public int getValuesCount() {
        if (values == null) {
            return 1;
        }

        return values.size();
    }
}