/*
 * Created on 06.12.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Mike
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class AccessControlList implements AccessController {

    private Map<Address, Address> addrs;

    /**
     *
     */
    public AccessControlList() {
        addrs = new TreeMap<Address, Address>();
    }

    public AccessControlList(List<Address> addrList) {
        addrs = new TreeMap<Address, Address>();

        for (Address a : addrList) {
            addAddress(a);
        }
    }

    public void addAddress(Address addr) {
        addrs.put(addr, addr);
    }

    /* (non-Javadoc)
     * @see com.pri.messenger.AccessController#checkAddress(com.pri.messenger.Address)
     */
    public boolean checkAddress(Address addr) {

        if (addrs.containsKey(addr)) {
            return true;
        }

        return false;
    }

    public boolean checkAddress(String local) {
        return addrs.containsKey(local);
    }

}
