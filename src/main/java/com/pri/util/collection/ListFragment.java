package com.pri.util.collection;

import java.util.List;

public class ListFragment<E> {

    private List<E> list;
    private int totalLength;

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }
}
