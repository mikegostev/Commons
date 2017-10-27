/*
 * Created on 24.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger.server;

import com.pri.adob.ADOB;
import com.pri.adob.ADOBFactory;
import com.pri.adob.AbstractADOB;
import com.pri.adob.ByteArrayADOB;
import com.pri.adob.FileADOB;
import com.pri.adob.InputStreamADOB;
import com.pri.log.Log;
import com.pri.log.Logger;
import com.pri.messenger.Address;
import com.pri.messenger.Message;
import com.pri.messenger.MessageDeliveryException;
import com.pri.messenger.MessageQueue;
import com.pri.messenger.Messenger;
import com.pri.messenger.NetworkException;
import com.pri.messenger.NetworkInputRequest;
import com.pri.messenger.NetworkMessengerProtocol;
import com.pri.messenger.NetworkMessengerProtocol.ReqType;
import com.pri.messenger.ProtocolException;
import com.pri.messenger.RecipientNotFoundException;
import com.pri.messenger.TransferUnit;
import com.pri.session.ClientSession;
import com.pri.session.SessionManager;
import com.pri.util.ProgressListener;
import com.pri.util.stream.LimitedInputStream;
import com.pri.util.stream.StreamPump;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.FactoryConfigurationError;

/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ClientServerMessenger extends ServerNetworkMessenger {

    private static final int MAX_POLL_THREAD_LIFETIME_SEC = 120;
    // private SessionPool sPool;
    private int msgCounter = 1;
    private Config config = new Config();
    private ADOBFactory adobFact = new MsgrADOBFactory();

    private static Logger logger = Log.getLogger(ClientServerMessenger.class);
    private SessionManager sessMngr;

    public ClientServerMessenger(Messenger local, SessionManager sm) {
        super(local);
        sessMngr = sm;
    }

    public void asyncSend(Message message) throws RecipientNotFoundException, NetworkException {
        String sessID = message.getAddress().getSession();

  /*
  UserCore uc = GlobalParameters.getDefault().getUserCore();
  
  if( uc == null )
   throw new NetworkException("Server not ready");
  */

        ClientSession cldata = sessMngr.getSession(sessID);

        if (cldata == null) {
            throw new RecipientNotFoundException();
        }

        message.setSync(false);

        cldata.getMessageQueue().enqueueMessage(message);
    }

    public void syncSend(Message message, @SuppressWarnings("unused") ProgressListener pLsnr)
            throws RecipientNotFoundException, NetworkException {
        syncSend(message);
    }

    @SuppressWarnings("unused")
    public void syncSend(Message message, ADOBFactory af) throws RecipientNotFoundException, NetworkException {
        throw new RuntimeException("Method not implemented");
    }

    @SuppressWarnings("unused")
    public void syncSend(Message message, ADOBFactory af, ProgressListener pLsnr)
            throws RecipientNotFoundException, NetworkException {
        throw new RuntimeException("Method not implemented");
    }

    public void syncSend(Message message) throws RecipientNotFoundException, NetworkException {
        message.setSync(true);
        message.setID(String.valueOf(msgCounter++));

        String sessID = message.getAddress().getSession();
  
  /*
  UserCore uc = GlobalParameters.getDefault().getUserCore();
  
  if( uc == null )
   throw new NetworkException("Server not ready");
  
  ClientSession cldata = uc.getClientData( sessID );
  */

        ClientSession cldata = sessMngr.getSession(sessID);

        if (cldata == null) {
            throw new RecipientNotFoundException(message.getAddress());
        }

        cldata.getMessageQueue().enqueueMessage(message);

        if (cldata.getWaitingRoom().waitResponse(message)) {
            if (message.countHandings() == 0 && message.getResponses() != null && message.getResponses().size() == 1) {
                Object mb = message.getResponses().iterator().next();

                if (mb instanceof RecipientNotFoundException) {
                    throw (RecipientNotFoundException) mb;
                }
            }

            return;
        }

        throw new NetworkException("Reply timeout");

    }

    public void processRequest(HttpConnection conn, ClientSession clData) {
        Message msg;
        TransferUnit tu;

        MessageQueue queue = clData.getMessageQueue();
        WaitingRoom wroom = clData.getWaitingRoom();

        try {
            InputStream connIS = null;

            if (config.getMaxMessageSize() > 0) {
                connIS = new LimitedInputStream(conn.getInputStream(), config.getMaxMessageSize());
            } else {
                connIS = conn.getInputStream();
            }

            NetworkInputRequest netreq = NetworkMessengerProtocol
                    .parseResponse(conn.getHeader("Content-type"), connIS, conn.getHeadersIterator(), adobFact);

            ReqType rType = netreq.getType();

            logger.debug("Got request: {0} from: {1} type: {2}", rType, clData.getUserName(), netreq.getMessageType());

            if (rType == ReqType.IDENTIFYREQ) {
                tu = NetworkMessengerProtocol.prepareIdentifyReply(
                        new Address(Address.NET_PEER_HOST, clData.getSessionString(), null).toString(),
                        MAX_POLL_THREAD_LIFETIME_SEC);

                conn.setHeader("Content-type", tu.getContentType());
                conn.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                conn.setHeader("Pragma", "no-cache");
                setResponseHeaders(conn, tu);

//    StreamPump.doPump(tu.getInputStream(), conn.getOutputStream());

                queue.setState(MessageQueue.State.READY);

                return;
            }

            if (rType == ReqType.POLL) {
                String threadName = null;
                if (logger.isDebugEnabled()) {
                    threadName = Thread.currentThread().getName();
                    Thread.currentThread().setName("POLL " + clData.getAddr());
                }

                MessageQueue.QueueElement qEl = queue.getMessage(MAX_POLL_THREAD_LIFETIME_SEC * 1000);
//System.out.println("End wait cycle");

                msg = qEl.getMessage();

                if (msg != null && logger.isDebugEnabled()) {
                    logger.debug("Sending message: type: {0} to: {1} command: {2}", msg.isSync() ? "SYNC" : "ASYNC",
                            msg.getAddress(), msg.getType());
                }

                if (qEl.getState() == MessageQueue.State.SESS_EXPIRED) {
                    tu = NetworkMessengerProtocol.prepareSessionExpired();
                } else if (qEl.getState() == MessageQueue.State.CLOSE_REQ) {
                    tu = NetworkMessengerProtocol.prepareCloseAckRequest();
                } else if (msg == null) {
                    tu = NetworkMessengerProtocol.prepareNOPRequest();
                } else if (msg.isSync()) {
                    tu = NetworkMessengerProtocol.prepareSyncRequest(msg);
                } else {
                    tu = NetworkMessengerProtocol.prepareAsyncRequest(msg);
                }

                try {
                    conn.setHeader("Content-type", tu.getContentType());
                    conn.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                    conn.setHeader("Pragma", "no-cache");
                    conn.setHeader(NetworkMessengerProtocol.X_MSG_QLEN, String.valueOf(qEl.getQueueLenth()));
                    conn.setHeader(NetworkMessengerProtocol.X_MSG_VER, String.valueOf(clData.getSessionIncUniq()));
                    setResponseHeaders(conn, tu);

                    InputStream is = tu.getInputStream();

                    if (is != null) {
                        StreamPump.doPump(is, conn.getOutputStream());
                    }
/*     if(is != null)
      StreamPump.doPump(is, conn.getOutputStream(), new PumpListener()
      {

       public void dataPumped(int k)
       {
        System.out.println("Data pumped: " + k);

       }
      });
*/
                    conn.getOutputStream().close();
                } catch (IOException e) {
                    queue.messagePushback(msg);
                    System.out.println("Exception during client calling. " + e);
                }

                if (logger.isDebugEnabled()) {
                    Thread.currentThread().setName(threadName);
                }

                return;
            }

            if (rType == ReqType.CLOSE) {
                queue.setState(MessageQueue.State.CLOSE_REQ);
                return;
            }

            if (rType == ReqType.SYNCREPLY || rType == ReqType.EXCEPTION) {
                msg = wroom.getMessage(netreq.getID());

                if (msg != null) {
                    synchronized (msg) {
                        msg.setResponses(netreq.getResponses());
                        msg.setHandingsCounter(netreq.getHandings());

                        msg.notify();
                    }
                }

                return;
            }

            if (rType == ReqType.SYNCREQUEST) {
                Address dstAddr = new Address(netreq.getAddress());

                msg = new Message(dstAddr);
                if (netreq.getResponses() != null) {
                    msg.setBodies(netreq.getResponses());
                } else {
                    msg.setBody(netreq.getBody());
                }
                msg.setSync(true);
                msg.setSenderData(clData);
                msg.setType(netreq.getMessageType());

                if (clData.getAccessController() == null || clData.getAccessController()
                        .checkAddress(dstAddr)) // || clData.getUserID() == 1
                {
                    try {
                        // msg.getAddress().setLocal();
                        // System.out.println("Message to: "+dstAddr);
                        localMessenger.syncSend(msg);

                        tu = NetworkMessengerProtocol.prepareSyncReply(msg);

                    } catch (MessageDeliveryException e) {
                        tu = NetworkMessengerProtocol.prepareException(msg, e);
                    }
                } else {
                    tu = NetworkMessengerProtocol.prepareException(msg, new com.pri.messenger.SecurityException(
                            "Address '" + netreq.getAddress() + "' prohibited by policy"));
                }

                conn.setHeader("Content-type", tu.getContentType());
                conn.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                conn.setHeader("Pragma", "no-cache");

                int sz = tu.getContentSize();

                if (sz > 0) {
                    conn.setHeader("Content-length", String.valueOf(sz));
                }

                setResponseHeaders(conn, tu);

                StreamPump.doPump(tu.getInputStream(), conn.getOutputStream());
                return;
            }

            if (rType == ReqType.ASYNCREQUEST) {
                Address dstAddr = new Address(netreq.getAddress());

                msg = new Message(new Address(netreq.getAddress()));
                if (netreq.getResponses() != null) {
                    msg.setBodies(netreq.getResponses());
                } else {
                    msg.setBody(netreq.getBody());
                }
                msg.setSync(false);
                msg.setSenderData(clData);
                msg.setType(netreq.getMessageType());

                // msg.getAddress().setLocal();
                if (clData.getAccessController() == null || clData.getAccessController().checkAddress(dstAddr)) {
                    try {
                        localMessenger.asyncSend(msg);
                    } catch (MessageDeliveryException e) {
                    }
                } else {
                    tu = NetworkMessengerProtocol.prepareException(msg, new com.pri.messenger.SecurityException(
                            "Address '" + netreq.getAddress() + "' prohibited by policy"));

                    conn.setHeader("Content-type", tu.getContentType());
                    conn.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                    conn.setHeader("Pragma", "no-cache");
                    setResponseHeaders(conn, tu);

                    StreamPump.doPump(tu.getInputStream(), conn.getOutputStream());
                }

                return;
            }

        } catch (FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void setResponseHeaders(HttpConnection conn, TransferUnit tu) {
        Iterator<Map.Entry<String, String>> iter = tu.iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> me = iter.next();
            conn.setHeader(me.getKey(), me.getValue());
        }
    }

    @Override
    public void destroy() {
    }

    static class ADOBOverHTTPInput extends InputStreamADOB {

        private boolean complete = false;

        public ADOBOverHTTPInput(String mimeType, InputStream is, long size) {
            super(mimeType, is, size);
        }

        public synchronized void waitStreamEnd() {
            while (!complete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }

        public synchronized void waitStreamEnd(long delay) {
            while (!complete) {
                try {
                    wait(delay);
                } catch (InterruptedException e) {
                }
            }
        }

        synchronized void signalEnd() {
            complete = true;
            notify();
        }

        class SignaledInputStream extends InputStream {

            private InputStream stream;

            SignaledInputStream(InputStream st) {
                stream = st;
            }

            @Override
            public int read() throws IOException {
                try {
                    int ch = stream.read();

                    if (ch == -1) {
                        signalEnd();
                    }

                    return ch;
                } catch (IOException e) {
                    signalEnd();
                    throw e;
                }
            }

            public int read(byte[] buf) throws IOException {
                try {
                    int ch = stream.read(buf);

                    if (ch == -1) {
                        signalEnd();
                    }

                    return ch;
                } catch (IOException e) {
                    signalEnd();
                    throw e;
                }
            }

            public int read(byte[] buf, int offs, int len) throws IOException {
                try {
                    int ch = stream.read(buf, offs, len);

                    if (ch == -1) {
                        signalEnd();
                    }

                    return ch;
                } catch (IOException e) {
                    signalEnd();
                    throw e;
                }
            }

        }
    }

    class MsgrADOBFactory implements ADOBFactory {

        public ADOB createADOB(String type, int size, String contID, String disp, InputStream is, Object meta,
                boolean metaSerial, boolean canDelay) throws IOException {
            if (size > 0 && size <= config.getMaxInlineBodySize()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamPump.doPump(is, baos);

                AbstractADOB adb = new ByteArrayADOB(baos.toByteArray(), type);
                adb.setDisposition(disp);
                adb.setContentID(contID);
                adb.setMetaInfo(meta);
                adb.setMetaSerialized(metaSerial);

                return adb;
            }

            if (canDelay) {
                AbstractADOB a = new ADOBOverHTTPInput(type, is, size);
                a.setContentID(contID);
                a.setDisposition(disp);
                a.setMetaInfo(meta);
                a.setMetaSerialized(metaSerial);

                return a;
            }

            File tFile = config.getTemporaryFile();

            try {
                StreamPump.doPump(is, new FileOutputStream(tFile));
            } catch (IOException t) {
                tFile.delete();
                throw t;
            }

            AbstractADOB a = new FileADOB(tFile, type, true);
            a.setContentID(contID);
            a.setDisposition(disp);
            a.setMetaInfo(meta);
            a.setMetaSerialized(metaSerial);

            return a;
        }
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
