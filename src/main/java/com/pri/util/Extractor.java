package com.pri.util;

public interface Extractor<Src, Dst> {

    Dst extract(Src obj);
}
