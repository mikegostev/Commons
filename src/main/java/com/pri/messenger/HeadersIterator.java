/*
 * Created on 05.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.util.StringPair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class HeadersIterator implements Iterator<StringPair> {

    // private Map map;
    private StringPair sp;
    Iterator<Map.Entry<String, List<String>>> mapIter;

    /**
     *
     */
    public HeadersIterator(Map<String, List<String>> m) {
//  map = m;
        sp = new StringPair();
        mapIter = m.entrySet().iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return mapIter.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public StringPair next() {
        Map.Entry<String, List<String>> me = mapIter.next();
        sp.setFirst(me.getKey());
        sp.setSecond(me.getValue().get(0));

        //System.out.println("Header: "+sp.getFirst()+"="+sp.getSecond());

        return sp;
    }

}
