package com.pri.util.sql;

import java.util.concurrent.atomic.AtomicInteger;

public class Field {

    public enum Type {

        ID(int.class), STRING(String.class), PASSWORD(String.class), REF(int.class), TEXT(String.class),
        BOOLEAN(boolean.class), INT(int.class), FLOAT(float.class), TIME(long.class), LONG(long.class);

        private Class<?> claz;

        Type(Class<?> c) {
            claz = c;
        }

        Class<?> getTypeClass() {
            return claz;
        }
    }

    private static AtomicInteger count = new AtomicInteger(1);

    private String name;
    private Type subtype;
    private boolean editable;
    private boolean listable;
    private int ordinal;

    public Field(String name, Type st, boolean edt, boolean lst) {
        this.name = name;
        subtype = st;
        editable = edt;
        listable = lst;
        ordinal = count.getAndIncrement();
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isListable() {
        return listable;
    }

    public void setListable(boolean listable) {
        this.listable = listable;
    }

    public Type getSubtype() {
        return subtype;
    }


}
