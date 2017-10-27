/*
 * Created on 07.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.session;

import com.pri.log.Log;
import com.pri.log.Logger;
import com.pri.util.CalendarUtils;
import com.pri.util.Interval;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class SessionPoolLocalMap<CS extends ClientSession> implements SessionPool<CS> {

    static private Logger log = Log.getLogger(SessionPoolLocalMap.class);

    int expTime;
    int maxLiveTime;

    Map<String, CS> pool;
    ExpiredSessionCleaner cleaner;
    ReadWriteLock rwLock;

    public SessionPoolLocalMap() {
        expTime = CS.EXPIRATON_TIME;
        maxLiveTime = CS.MAX_LIVE_TIME;
        init();
    }

    public SessionPoolLocalMap(int expT, int maxT) {
        expTime = expT;
        maxLiveTime = maxT;
        init();
    }

    private void init() {
        pool = new TreeMap<String, CS>();
        cleaner = new ExpiredSessionCleaner();
        //  semaphore = new Semaphore();
        rwLock = new ReentrantReadWriteLock();
        cleaner.start();
    }

    public String createSession(CS cl) throws UnableGenerateSessionKeyException {
        String sk = createHashString(cl.getAddr());

        rwLock.writeLock().lock();
        pool.put(sk, cl);
        rwLock.writeLock().unlock();

        cl.setSessionString(sk);

        log.trace("Session created for user: " + cl.getUserName());
        return sk;
    }

    public CS verifySession(String k, RequestData rq) {
        rwLock.readLock().lock();
        CS storedClientData = pool.get(k);
        rwLock.readLock().unlock();

        if (storedClientData != null && storedClientData.sameSource(rq)) {
            storedClientData.update();
            return storedClientData;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pri.shex.backend.SessionPool#removeExpiredSessions()
     */
    public void removeExpiredSessions() {
        rwLock.writeLock().lock();

        Object[] kArr = pool.keySet().toArray();

        long ts = System.currentTimeMillis();

        // Log.info("Cleaning expired sessions. expTime="+(new
        // java.util.Date(ts-1000*expTime).toString()));

        for (int i = 0, l = kArr.length; i < l; i++) {
            CS cl = pool.get(kArr[i]);

            if (log.isTraceEnabled()) {
                log.trace("Checking client " + cl.getUserName() + "(" + cl.getUserID() + ")@" + cl.getAddr()
                        + " lastUpdTime=" + (new java.util.Date(cl.lastUpdateTimestamp()).toString()));
            }

            if (cl.lastUpdateTimestamp() + 1000 * expTime < ts || cl.creationTimestamp() + 1000 * maxLiveTime < ts) {
                pool.remove(kArr[i]);

                try {
                    cl.destroy();
                } catch (Throwable t) {
                    log.error("Exception during expired session destuction", t);
                    t.printStackTrace();
                }

                if (log.isTraceEnabled()) {
                    log.trace("Expired: " + cl.getUserName() + "(" + cl.getUserID() + ")@" + cl.getAddr());
                }
            }
        }

        rwLock.writeLock().unlock();
    }

 /*
  * public SessionInfo[] listSessions() { semaphore.getReadLock();
  * 
  * 
  * Collection sess = pool.values(); SessionInfo[] sessList = new
  * SessionInfo[sess.size()];
  * 
  * Iterator it = sess.iterator();
  * 
  * int i=0; while( it.hasNext() ) { CS dt =
  * (CS)it.next(); sessList[i++] = new SessionInfo(
  * dt.creationTimestamp(), dt.lastUpdateTimestamp(),
  * dt.lastUpdateTimestamp()+1000*expTime, dt.getUserName(), dt.getAddr() ); }
  * 
  * semaphore.releaseReadLock();
  * 
  * return sessList; }
  */

    public List<CS> listSessions() {
        rwLock.readLock().lock();

        // Collection sess = pool.values();
        List<CS> sessList = new ArrayList<CS>(pool.values());

        rwLock.readLock().unlock();

        return sessList;
    }

    public List<CS> listSessions(List<Object> uIDs, String sessK, String addr, Interval startTimeIval,
            Interval updateTimeIval, Interval expireTimeIval) {
        rwLock.readLock().lock();

        Pattern sKeyPat = null;
        Pattern addrPat = null;

        if (sessK != null) {
            sKeyPat = Pattern.compile(sessK);
        }

        if (addr != null) {
            addrPat = Pattern.compile(addr);
        }

        List<CS> sessList = new ArrayList<CS>();

        for (CS dt : pool.values()) {
            if (addrPat != null && !addrPat.matcher(dt.getAddr()).matches()) {
                continue;
            }

            if (sKeyPat != null && !addrPat.matcher(dt.getSessionString()).matches()) {
                continue;
            }

            if (uIDs != null && uIDs.size() > 0) {
                boolean found = false;
                int uid = dt.getUserID();

                for (Object o : uIDs) {

                    if (o instanceof Integer) {
                        if (((Integer) o).intValue() == uid) {
                            found = true;
                            break;
                        }
                    } else if (o instanceof Interval) {
                        if (((Interval) o).contains(uid)) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    continue;
                }
            }

            if (startTimeIval != null && !startTimeIval
                    .contains(CalendarUtils.epoch70ToEpoch2kSec(dt.creationTimestamp()))) {
                continue;
            }

            if (updateTimeIval != null && !updateTimeIval
                    .contains(CalendarUtils.epoch70ToEpoch2kSec(dt.creationTimestamp()))) {
                continue;
            }

            if (expireTimeIval != null && !expireTimeIval
                    .contains(CalendarUtils.epoch70ToEpoch2kSec(dt.creationTimestamp()))) {
                continue;
            }

            sessList.add(dt);

   /*
    * sessList[i++] = new SessionInfo( dt.creationTimestamp(),
    * dt.lastUpdateTimestamp(), dt.lastUpdateTimestamp()+1000*expTime,
    * dt.getUserName(), dt.getAddr() );
    */
        }

        rwLock.readLock().unlock();

        return sessList;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pri.shex.backend.SessionPool#dropSession(int, java.lang.String,
     *      java.util.Calendar)
     */
    public boolean dropSession(String sk) {
        rwLock.writeLock().lock();

        try {
            CS cl = pool.get(sk);

            if (cl != null) {
                pool.remove(sk);
                cl.destroy();
            }

            if (cl == null) {
                return false;
            }
        } finally {
            rwLock.writeLock().unlock();
        }

        return true;
    }

    class ExpiredSessionCleaner extends Thread {

        private boolean alive = true;

        public ExpiredSessionCleaner() {
            super("ExpiredSessionCleaner");
        }

        public void run() {
            while (alive) {
                try {
                    sleep(1000 * expTime / 2);
                } catch (InterruptedException ex) {
                }

                removeExpiredSessions();
            }
        }

        public void terminate() {
            alive = false;
            interrupt();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pri.shex.backend.SessionPool#getClientData()
     */
    public CS getClientData(String k) {
        rwLock.readLock().lock();
        CS storedClientData = pool.get(k);
        rwLock.readLock().unlock();

        return storedClientData;
    }

    public void destroy() {
        try {
            rwLock.writeLock().lock();

            Iterator<Map.Entry<String, CS>> iter = pool.entrySet().iterator();
            while (iter.hasNext()) {
                CS cl = iter.next().getValue();

                if (cl != null) {
                    log.trace("  Destroying session of: " + cl.getUserName());
                    cl.destroy();
                    log.trace("  Destroying session of: " + cl.getUserName() + "  ...done");
                }

                iter.remove();
            }
        } finally {
            rwLock.writeLock().unlock();
        }

        cleaner.terminate();
    }

    public CS getSession(int userID) {
        try {
            rwLock.readLock().lock();

            for (CS cs : pool.values()) {
                if (cs.getUserID() == userID) {
                    return cs;
                }
            }

        } finally {
            rwLock.readLock().unlock();
        }

        return null;
    }


    public CS getSessionByUser(String uname) {
        try {
            rwLock.readLock().lock();

            for (CS cs : pool.values()) {
                if (cs.getUserName() == uname) {
                    return cs;
                }
            }

        } finally {
            rwLock.readLock().unlock();
        }

        return null;
    }

    public void dropUserSession(int userID) {
        String sk = null;

        try {
            rwLock.readLock().lock();
            for (Map.Entry<String, CS> me : pool.entrySet()) {
                if (me.getValue().getUserID() == userID) {
                    sk = me.getKey();
                    break;
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }

        if (sk != null) {
            rwLock.writeLock().lock();
            CS cs = pool.remove(sk);
            rwLock.writeLock().unlock();

            if (cs != null) {
                cs.destroy();
            }
        }

    }

    public void dropUserSession(String user) {
        rwLock.readLock().lock();
        String sk = null;
        for (Map.Entry<String, CS> me : pool.entrySet()) {
            if (me.getValue().getUserName().equals(user)) {
                sk = me.getKey();
                break;
            }

        }
        rwLock.readLock().unlock();

        if (sk != null) {
            rwLock.writeLock().lock();
            CS cs = pool.remove(sk);
            rwLock.writeLock().unlock();

            if (cs != null) {
                cs.destroy();
            }

        }
    }

    private String createHashString(String str) throws UnableGenerateSessionKeyException {
        StringBuffer message = new StringBuffer(100);

        message.append(str);

        message.append(System.currentTimeMillis());

        try {
            MessageDigest md5d = MessageDigest.getInstance("MD5");

            byte[] digest = md5d.digest(message.toString().getBytes());

            message.setLength(0);

            for (int i = 0; i < digest.length; i++) {
                message.append(Integer.toString(digest[i] & 0xFF, 36));
            }

            return "K" + message;
        } catch (NoSuchAlgorithmException ex) {
            throw new UnableGenerateSessionKeyException("Invalid digest algorithm: MD5");
        }
    }


}
