package com.pri.messenger;

import java.util.ArrayList;

public class ListMessageBody extends ArrayList<Object> implements SerializableMessageBody {

    public ListMessageBody() {
        super(5);
    }

    public ListMessageBody(int sz) {
        super(sz);
    }

    public String toXML() {
        // TODO Auto-generated method stub
        return null;
    }

}
