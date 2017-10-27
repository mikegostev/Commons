/*
 * Created on 07.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.session;

import com.pri.messenger.AccessController;
import com.pri.messenger.Address;
import com.pri.messenger.MessageQueue;
import com.pri.messenger.server.DataWaiter;
import com.pri.messenger.server.WaitingRoom;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;


public class ClientSession {

    public final static int EXPIRATON_TIME = 60 * 4;
    public final static int MAX_LIVE_TIME = 60 * 60 * 24;

    private String IPaddr;
    private UserProfile usrProf;

    private String language = null;

    private String sessionString;

    private long lastUpdateTimestamp;

    private long creationTimestamp;

    private MessageQueue mqueue;

    private WaitingRoom wroom;

// private List<ServiceInfo>             services;

    private AccessController accessController;

    private DataWaiter dWtr;

    private Address msgrAddr;
    private AtomicInteger uniqGen;
    private SecretKeySpec aesKey;

    // private ServiceConfigurationPool confPool;
    static private DataSource dataSource;

    private List<SessionStateListener> stateListeners;

    private Map<String, Object> env;

    private static final KeyGenerator kgen;

    static {
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("AES");
            kg.init(128);
        } catch (NoSuchAlgorithmException e) {
        }
        kgen = kg;
    }

    public ClientSession(UserProfile up, String addr, String lang) {
        usrProf = up;
        uniqGen = new AtomicInteger(1);
        IPaddr = addr;
        language = lang.toUpperCase();

        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();

        aesKey = new SecretKeySpec(raw, "AES");

        lastUpdateTimestamp = creationTimestamp = System.currentTimeMillis();

    }


    public void update() {
        lastUpdateTimestamp = System.currentTimeMillis();
    }

    public void destroy() {
        if (stateListeners != null) {
            System.out.println(
                    "   Informing SessionStateListeners of " + getUserName() + " Total: " + stateListeners.size());
            for (SessionStateListener ssl : stateListeners) {
                System.out.println("    Informing " + ssl.getClass().getName());
                ssl.sessionClosed(getUserID());
            }
        }
        System.out.println("   Informed SessionStateListeners of " + getUserName());

        System.out.println("   Destroying mqueue of " + getUserName());
        if (mqueue != null) {
            mqueue.destroy();
        }
        System.out.println("   Destroyed mqueue of " + getUserName());

        System.out.println("   Destroying wroom of " + getUserName());
        if (wroom != null) {
            wroom.destroy();
        }
        System.out.println("   Destroyed wroom of " + getUserName());

    }

    public void setAddr(String adr) {
        IPaddr = adr;
    }

    public String getAddr() {
        return IPaddr;
    }

    public String getUserName() {
        return usrProf.getUserName();
    }

    public long lastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public long creationTimestamp() {
        return creationTimestamp;
    }

    public MessageQueue getMessageQueue() {
        if (mqueue == null) {
            mqueue = new MessageQueue();
        }

        return mqueue;
    }

    public WaitingRoom getWaitingRoom() {
        if (wroom == null) {
            wroom = new WaitingRoom();
        }

        return wroom;
    }

    /**
     * @return Returns the sessionString.
     */
    public String getSessionString() {
        return sessionString;
    }

    /**
     * @param sessionString The sessionString to set.
     */
    public void setSessionString(String sessionString) {
        this.sessionString = sessionString;
        msgrAddr = Address.newClientAddress(sessionString, null);
    }

    /**
     * @return Returns the dataSource.
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param ds The dataSource to set.
     */
    public static void setDataSource(DataSource ds) {
        ClientSession.dataSource = ds;
    }

    /**
     * @return Returns the userID.
     */
    public int getUserID() {
        return usrProf.getUserID();
    }

    /**
     * @return Returns the confPool.
     */
    // public ServiceConfigurationPool getConfigurationManager()
    // {
    // return confPool;
    // }
    /**
     * @return Returns the services.
     */
// public List<ServiceInfo> getServices()
// {
//  return services;
// }

    /**
     * @return Returns the accessController.
     */
    public AccessController getAccessController() {
        return accessController;
    }

    public void setAccessController(AccessController ac) {
        accessController = ac;
    }

    public void addSessionStateListener(SessionStateListener ssl) {
        if (stateListeners == null) {
            stateListeners = new LinkedList<SessionStateListener>();
        }

        stateListeners.add(ssl);
    }

    public void removeSessionStateListener(SessionStateListener ssl) {
        stateListeners.remove(ssl);
    }

    public DataWaiter getDataWaiter() {
        if (dWtr == null) {
            dWtr = new DataWaiter();
        }

        return dWtr;
    }

    public Address getMessengerAddr() {
        return msgrAddr;
    }

    public Object getEnv(String key) {
        if (env == null) {
            return null;
        }

        return env.get(key);
    }

    public void setEnv(String key, Object val) {
        if (env == null) {
            env = new TreeMap<String, Object>();
        }

        env.put(key, val);
    }

    public int getSessionIncUniq() {
        return uniqGen.getAndIncrement();
    }

    public SecretKeySpec getAesKey() {
        return aesKey;
    }

    public String toString() {
        return getUserName() + " ID=" + getUserID();
    }

    public String getLanguage() {
        return language;
    }


    public UserProfile getUserProfile() {
        return usrProf;
    }

    public boolean sameSource(RequestData rq) {
        return getAddr().equals(rq.getAddr());
    }


}
