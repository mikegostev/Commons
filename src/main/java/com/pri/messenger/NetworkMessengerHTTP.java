/*
 * Created on 30.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.adob.ADOBFactory;
import com.pri.log.Log;
import com.pri.log.Logger;
import com.pri.messenger.ConnectionStateListener.State;
import com.pri.messenger.NetworkMessengerProtocol.ReqType;
import com.pri.util.ProgressListener;
import com.pri.util.stream.LimitedInputStream;
import com.pri.util.stream.ListenedInputStream;
import com.pri.util.stream.PumpListener;
import com.pri.util.stream.StreamPump;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class NetworkMessengerHTTP extends ClientNetworkMessenger implements Runnable {

    private static Logger logger = Log.getLogger(NetworkMessengerHTTP.class);

    private static final int MAX_CONNECT_TRIES = 5;
    private static final int MAX_POLL_LIFETIME = 30 * 60 * 1000;
    private static final int MIN_POLL_LIFETIME = 10 * 1000;
    private static final int MAX_SIMULTANEOUS_POLLS = 5;
    private static final int POLL_MARGIN = 5 * 1000;

    private static final int MAX_MESSAGE_SIZE = 4000000;

    private URL serverURL;
    private Thread myThread;
    private ExecutorService syncSendThPool = Executors.newCachedThreadPool();
    private boolean connected = false;
    private List<ConnectionStateListener> csLsnrs;
    private volatile State currentState;
    private long pollLifeTime = MAX_POLL_LIFETIME;
    private volatile int workersStarted = 0;
    private volatile State lastPollStatus;
    private int serverQueueLen = 0;
    private long lastWorkerStartTime = 0;
    private int infoVer;

    public NetworkMessengerHTTP(Messenger msgr, URL sUrl) throws Exception {
        super(msgr);
        serverURL = sUrl;

        csLsnrs = new LinkedList<ConnectionStateListener>();
        currentState = lastPollStatus = State.DISCONNECTED;
    }

    private HttpURLConnection sendTU(TransferUnit tu) throws NetworkException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) serverURL.openConnection();
            conn.setRequestMethod(tu.getMethod());
//   conn.setRequestProperty("Content-type",tu.getContentType());
//   conn.setRequestProperty("Cache-Control","no-store, no-cache, must-revalidate");
//   conn.setRequestProperty("Pragma","no-cache");

            setRequestProperties(conn, tu);

            int len = tu.getContentSize();

            if (len >= 0) {
                conn.setRequestProperty("Content-Length", String.valueOf(len));
            }

            InputStream is = tu.getInputStream();

            if (is != null) {
                conn.setDoOutput(true);
                conn.connect();
                StreamPump.doPump(is, conn.getOutputStream());
            } else {
                conn.connect();
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new NetworkException("Messenger rejects connection. Possible reason: invalid or expired session");
            }

            return conn;
        } catch (IOException e) {
            throw new NetworkException(e.getMessage());
        }

    }

    public State getState() {
        return currentState;
    }

    public boolean setConnected(boolean state) throws NetworkException {
        if (state) {
            if (connected) {
                return true;
            }

            TransferUnit tu = NetworkMessengerProtocol.prepareIdentifyRequest();
            HttpURLConnection conn;
            try {
                conn = sendTU(tu);

                NetworkInputRequest netreq = NetworkMessengerProtocol
                        .parseResponse(conn.getContentType(), conn.getInputStream(),
                                new HeadersIterator(conn.getHeaderFields()), null);

                setNetworkAddress(new Address(netreq.getAddress()));
                setPollLifetime(netreq.getPollLifetime());

                connected = true;
                myThread = new Thread(this);
                myThread.start();

                return true;
            } catch (Exception e) {
                throw new NetworkException(e.getMessage());
            }
        }

        if (myThread != null) {
            TransferUnit tu = NetworkMessengerProtocol.prepareCloseRequest();
            sendTU(tu);
            connected = false;

        }

        return connected = false;
    }


    private void setPollLifetime(int lt) {
        long llt = lt * 1000;
        logger.debug("Setting poll timeout: {0}", llt);
        if (llt <= MAX_POLL_LIFETIME && llt >= MIN_POLL_LIFETIME) {
            pollLifeTime = llt;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pri.util.AsyncTransmitter#asyncSend(com.pri.util.Message)
     */
    public void asyncSend(Message message) throws NetworkException {
        if (currentState != State.CONNECTED) {
            throw new NetworkException("Network messenger not connected");
        }

        HttpURLConnection conn;
        try {
            TransferUnit tu = NetworkMessengerProtocol.prepareAsyncRequest(message);

            conn = (HttpURLConnection) serverURL.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", tu.getContentType());

            setRequestProperties(conn, tu);

            conn.setDoOutput(true);

            StreamPump.doPump(tu.getInputStream(), conn.getOutputStream());

            conn.getInputStream();
            conn.disconnect();
        } catch (IOException e1) {
        }

    }

    public void syncSend(Message message) throws RecipientNotFoundException, NetworkException {
        syncSend(message, null, null);
    }

    public void syncSend(Message message, ProgressListener pLsnr) throws RecipientNotFoundException, NetworkException {
        syncSend(message, null, pLsnr);
    }

    public void syncSend(Message message, ADOBFactory af) throws RecipientNotFoundException, NetworkException {
        syncSend(message, af, null);
    }


    public void syncSend(Message message, ADOBFactory adobFact, final ProgressListener pLsnr)
            throws RecipientNotFoundException, NetworkException {
        if (currentState != State.CONNECTED) {
            throw new NetworkException("Network messenger not connected");
        }

        HttpURLConnection conn;
        try {
            TransferUnit tu = NetworkMessengerProtocol.prepareSyncRequest(message);

            conn = (HttpURLConnection) serverURL.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", tu.getContentType());

            int tSize = tu.getContentSize();

            if (tSize > 0) {
                conn.setFixedLengthStreamingMode(tSize);
                conn.setRequestProperty("Content-length", String.valueOf(tSize));
            }

            setRequestProperties(conn, tu);

            conn.setDoOutput(true);

            InputStream conIS;

            if (pLsnr != null) {

                if (tSize > 0) {
                    pLsnr.dataTransferSizeKnown(tSize);
                }

                pLsnr.startTransfer();
                StreamPump.doPump(tu.getInputStream(), conn.getOutputStream(), new PumpListener() {
                    public void dataPumped(int k) {
                        pLsnr.dataTransfered(k);
                    }

                    public void endOfStream() {
                        pLsnr.endTransfer();
                    }
                });

                tSize = conn.getContentLength();

                if (tSize > 0) {
                    pLsnr.dataReceiveSizeKnown(tSize);
                }

                conIS = new ListenedInputStream(conn.getInputStream(), new PumpListener() {
                    public void dataPumped(int k) {
                        pLsnr.dataReceived(k);
                    }

                    public void endOfStream() {
                        pLsnr.endReceive();
                    }
                }, tSize);

                pLsnr.startReceive();
            } else {
                StreamPump.doPump(tu.getInputStream(), conn.getOutputStream());
                conIS = conn.getInputStream();
            }

            if (adobFact == null) {
                conIS = new LimitedInputStream(conIS, MAX_MESSAGE_SIZE);
            }

            NetworkInputRequest netreq = NetworkMessengerProtocol
                    .parseResponse(conn.getContentType(), conIS, new HeadersIterator(conn.getHeaderFields()), adobFact);

//   if( pLsnr != null)
//    pLsnr.endReceive();
//System.out.println("Got message from server. Type: "+netreq.getType()+" Hndgs: "+netreq.getHandings());    

            message.setResponses(netreq.getResponses());
            message.setHandingsCounter(netreq.getHandings());

            if (message.countHandings() == 0 && message.getResponses() != null && message.getResponses().size() == 1) {
                MessageBody mb = message.getResponses().get(0);

                if (mb instanceof RecipientNotFoundException) {
                    throw (RecipientNotFoundException) mb;
                } else if (mb instanceof SecurityException) {
                    throw new NetworkException(mb.toString());
                }
            }

        } catch (IOException e1) {
            throw new NetworkException(e1);
        } catch (ProtocolException e) {
            throw new NetworkException(e);
        }

    }


    private void setRequestProperties(HttpURLConnection conn, TransferUnit tu) {
        Iterator<Map.Entry<String, String>> iter = tu.iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> me = iter.next();
            conn.setRequestProperty(me.getKey(), me.getValue());
        }

        conn.setRequestProperty("Content-type", tu.getContentType());
        conn.setRequestProperty("Cache-Control", "no-store, no-cache, must-revalidate");
        conn.setRequestProperty("Pragma", "no-cache");
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        int serverQueueLenL;
        int workersStartedL;

        fireConnState(State.CONNECTED);
        lastPollStatus = State.CONNECTED;

        while (connected) {
            synchronized (serverURL) {
                serverQueueLenL = serverQueueLen;
                workersStartedL = workersStarted;
            }

            int avail = MAX_SIMULTANEOUS_POLLS - workersStartedL;
            int need = serverQueueLenL - workersStartedL;
            if (need > 0) {
                if (avail > 0) {
                    startWorkers(serverQueueLenL < avail ? serverQueueLenL : avail);
                }
            } else if ((pollLifeTime - (System.currentTimeMillis() - lastWorkerStartTime)) <= POLL_MARGIN) {
                startWorkers(1);
            } else if (workersStartedL < 1) {
                startWorkers(1);
            }

            State lastSt;

            synchronized (serverURL) {
                try {
                    serverURL.wait(pollLifeTime - POLL_MARGIN);
                } catch (InterruptedException e) {
                }

                lastSt = lastPollStatus;
            }

/*   
System.out.println("Master poll thread awaked. connected="+connected+" lastStatus="+lastPollStatus
  +" infoVer="+infoVer+" queueLen="+serverQueueLenL+" workers="+workersStarted+" pollLifetime="+pollLifeTime
  +" delta="+((System.currentTimeMillis() - lastWorkerStartTime)/1000));   
*/

            if (!connected) {
                lastSt = State.SESSION_CLOSED;
            }

            fireConnState(lastSt);

            if (lastSt != State.CONNECTED) {
                connected = false;
                myThread = null;
                return;
            }
        }

        fireConnState(State.SESSION_CLOSED);

    }

    public void startWorkers(int n) {
        for (int i = 0; i < n; i++) {
//System.out.println("Starting poll worker");  

            workersStarted++;
            syncSendThPool.execute(new PollWorker());
        }
    }

    @Override
    public void destroy() {
        try {
            setConnected(false);
        } catch (NetworkException e) {
        }

        syncSendThPool.shutdown();
        connected = false;
    }

    private void fireConnState(State st) {
        synchronized (csLsnrs) {
            if (currentState == st) {
                return;
            }

            currentState = st;

            for (ConnectionStateListener l : csLsnrs) {
                l.stateStateChanged(st);
            }
        }
    }

    @Override
    synchronized public void addConnectionStateListenerListener(ConnectionStateListener l) {
        synchronized (csLsnrs) {
            csLsnrs.add(l);
        }
    }

    @Override
    synchronized public void removeConnectionStateListenerListener(ConnectionStateListener l) {
        synchronized (csLsnrs) {
            csLsnrs.remove(l);
        }
    }


    class SyncSendStub implements Runnable {

        Message msg;

        public SyncSendStub(Message m) {
            msg = m;
        }

        public void run() {
//   String xml=null;
            TransferUnit tu = null;

            try {

                localMessenger.syncSend(msg);
                tu = NetworkMessengerProtocol.prepareSyncReply(msg);

            } catch (MessageDeliveryException e) {
                tu = NetworkMessengerProtocol.prepareException(msg, e);
            } catch (IOException e) {
                tu = NetworkMessengerProtocol.prepareException(msg,
                        new MessageDeliveryException("IOException during delivery on peer side: " + e));
            }

            //System.out.println("Repling to: "+msg.getID()+" Stub: "+this);

            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) serverURL.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", tu.getContentType());

                setRequestProperties(conn, tu);

                conn.setDoOutput(true);

                StreamPump.doPump(tu.getInputStream(), conn.getOutputStream());

                conn.getInputStream();
            } catch (IOException e1) {
                logger.error("SYNCREPLY failed: " + msg.getAddress(), e1);
            }

        }
    }


    private class PollWorker implements Runnable {

        public void run() {
            synchronized (serverURL) {
                lastWorkerStartTime = System.currentTimeMillis();
            }

            HttpURLConnection conn = null;
            int connectTries = 0;

            while (connected && connectTries <= MAX_CONNECT_TRIES) {
                connectTries++;

                try {
                    TransferUnit tu = NetworkMessengerProtocol.preparePoll();

                    conn = (HttpURLConnection) serverURL.openConnection();
                    conn.setReadTimeout(0);
                    conn.setRequestMethod(tu.getMethod());
//     conn.setRequestProperty("Content-type",tu.getContentType());

                    setRequestProperties(conn, tu);

//     conn.setDoOutput(true);
                    conn.connect();

//     StreamPump.doPump(tu.getInputStream(),conn.getOutputStream());

                    int respCode = conn.getResponseCode();
// System.out.println("RespCode: "+respCode);

                    if (respCode == HttpURLConnection.HTTP_FORBIDDEN) {
                        synchronized (serverURL) {
                            conn.disconnect();
                            workersStarted--;
                            lastPollStatus = State.SESSION_EXPIRED;
                            infoVer = Integer.MAX_VALUE;
                            serverURL.notify();
                        }
                        return;
                    }

                    if (respCode != HttpURLConnection.HTTP_OK) {
                        conn.disconnect();

                        if (respCode == HttpURLConnection.HTTP_GATEWAY_TIMEOUT
                                || respCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                            connectTries--;
                        } else {
                            Thread.sleep(3 * 1000);
                        }

                        continue;
                    }

                    NetworkInputRequest netreq = NetworkMessengerProtocol
                            .parseResponse(conn.getContentType(), conn.getInputStream(),
                                    new HeadersIterator(conn.getHeaderFields()), null);

                    connectTries = 0;

                    ReqType rType = netreq.getType();

                    if (logger.isTraceEnabled()) {
                        logger.trace("Got message from server. Type: " + rType + " Dest: " + netreq.getAddress());
                    }

                    if (rType == ReqType.CLOSEACK) {
                        synchronized (serverURL) {
                            lastPollStatus = State.SESSION_CLOSED;
                            infoVer = Integer.MAX_VALUE;
                            workersStarted--;
                            serverURL.notify();
                        }
                        return;
                    }

                    if (rType == ReqType.SESSEXPIRED) {
                        synchronized (serverURL) {
                            conn.disconnect();
                            workersStarted--;
                            lastPollStatus = State.SESSION_EXPIRED;
                            infoVer = Integer.MAX_VALUE;
                            serverURL.notify();
                        }
                        return;
                    }

                    int ver = netreq.getInfoVersion();
                    int qLen = netreq.getQueueLength();

                    if (rType == ReqType.ASYNCREQUEST || rType == ReqType.SYNCREQUEST) {
                        Message m = new Message(new Address(netreq.getAddress()));

                        if (netreq.getResponses() != null) {
                            m.setBodies(netreq.getResponses());
                        } else {
                            m.setBody(netreq.getBody());
                        }

                        m.setID(netreq.getID());
                        m.setType(netreq.getMessageType());

                        if (rType == ReqType.ASYNCREQUEST) {
                            m.setSync(false);
                            try {
                                localMessenger.asyncSend(m);
                            } catch (MessageDeliveryException e) {
                                logger.error("Async delivery failed. Dest: " + m.getAddress());
                            }
                        } else if (rType == ReqType.SYNCREQUEST) {
                            m.setSync(true);

                            SyncSendStub ssst = new SyncSendStub(m);
                            syncSendThPool.execute(ssst);
                        }
                    } else if (rType != ReqType.NOP) {
                        throw new Exception("Invalid poll response: " + rType);
                    }

                    synchronized (serverURL) {
                        lastPollStatus = State.CONNECTED;
                        if (infoVer < ver) {
                            infoVer = ver;
                            serverQueueLen = qLen;
                        }
                        workersStarted--;
                        serverURL.notify();
                    }
                    return;

                } catch (Exception e) {
                    logger.debug("Poll exception: " + e, e);

                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e1) {
                    }
                }

            }

            synchronized (serverURL) {
                lastPollStatus = State.NETWORK_ERROR;
                workersStarted--;
                serverURL.notify();
            }

        }

    }


}
