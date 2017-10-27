/*
 * Created on 25.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger.server;

import com.pri.messenger.Message;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class WaitingRoom {

    static final int MAX_WAIT_TIME = 60 * 1000;
    private Map<String, Message> anticipants;

    public WaitingRoom() {
        anticipants = Collections.synchronizedMap(new TreeMap<String, Message>());
    }

    public void destroy() {
        if (anticipants == null) {
            return;
        }

        synchronized (anticipants) {
            if (anticipants.size() > 0) {
                Iterator iter = anticipants.values().iterator();
                while (iter.hasNext()) {
                    Object wm = iter.next();
                    synchronized (wm) {
                        notify();
                    }
                }

                anticipants.clear();
                anticipants = null;
            }
        }
    }

    public void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public boolean waitResponse(Message m) {
        m.resetHandingsCounter();
        anticipants.put(m.getID(), m);

        synchronized (m) {
            long ctime = System.currentTimeMillis();

            try {
                m.wait(MAX_WAIT_TIME);
            } catch (InterruptedException e) {
            }

            if (System.currentTimeMillis() - ctime >= MAX_WAIT_TIME) {
                return false;
            }
        }

        return true;

    }

    public Message getMessage(String id) {
        return anticipants.remove(id);
    }

}
