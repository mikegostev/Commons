package com.pri.messenger;

import com.pri.adob.ADOBFactory;
import com.pri.log.Log;
import com.pri.util.ProgressListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Same as LocalDelicery
//but this implementation supports some additional
//features like address handles and a set of threads
//to work with message queues. Supposed to be faster
//and more reliable then LocalDelivery.
public class ThreadPoolDelivery extends Messenger implements Runnable {

    private MonitorAddressResolver resolver = new HashMonitorAddressResolver();
    private List<Message> messageQueue = new LinkedList<Message>();
    private ExecutorService threadPool;

    public ThreadPoolDelivery() {
        super();
        threadPool = Executors.newCachedThreadPool();
    }

    public ThreadPoolDelivery(int nthreads) {
        super();
        threadPool = Executors.newFixedThreadPool(nthreads);
    }

    public ThreadPoolDelivery(ExecutorService threadSrv) {
        super();
        threadPool = threadSrv;
    }

    /*
    public int getThreadPoolLocksCount()
    {
     return threadPool.getNLocks();
    }

    public long getThreadPoolWaitTime()
    {
     return threadPool.getTotalWaitTime();
    }

    public int getThreadPoolNReqs()
    {
     return threadPool.getNReqs();
    }
   */
    public void syncSend(Message message) throws RecipientNotFoundException {
        syncSend(message, (ProgressListener) null);
    }

    public void syncSend(Message message, ProgressListener pLsnr) throws RecipientNotFoundException {
        message.setSync(true);
        generalSend(message, pLsnr, null);
    }

    public void syncSend(Message message, ADOBFactory af) throws RecipientNotFoundException {
        message.setSync(true);
        generalSend(message, null, af);
    }

    public void syncSend(Message message, ADOBFactory af, ProgressListener pLsnr) throws RecipientNotFoundException {
        message.setSync(true);
        generalSend(message, pLsnr, af);
    }

    public void asyncSend(Message message) {
        message.setSync(false);
        synchronized (messageQueue) {
            messageQueue.add(message);
        }
        threadPool.execute(this);
    }

    public Message syncReceive(Address address) {
        RecipientMonitor monitor = resolver.addSyncRecipient(address);
        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return monitor.message;
    }

    public Message syncReceive(Address address, long timeout) throws SyncReceiveTimeoutException {
        RecipientMonitor monitor = resolver.addSyncRecipient(address);
        synchronized (monitor) {
            try {
                if (timeout <= 0) {
                    monitor.wait();
                } else {
                    long ctime = System.currentTimeMillis();
                    monitor.wait(timeout);
                    if (System.currentTimeMillis() - ctime >= timeout) {
                        throw new SyncReceiveTimeoutException(timeout);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return monitor.message;
    }

    public void addRecipient(MessageRecipient recipient, Address address) {
        resolver.addRecipient(recipient, address);
    }

    public void run() {
        Message message = null;
        synchronized (messageQueue) {
            if (messageQueue.size() == 0) {
                return;
            }

            message = messageQueue.remove(0);
        }
        try {
            generalSend(message, null, null);
        } catch (MessageDeliveryException e) {
            Log.warn("Message was not delivered to: " + message.getAddress().toString(), e);
        }

    }

    @SuppressWarnings("unused")
    private void generalSend(Message message, ProgressListener pLsnr, ADOBFactory af)
            throws RecipientNotFoundException {

        // System.out.println("Local: "+message.getAddress().getLocal()+" Data:
        // "+message.getBody().getClass().getName());

        MonitorsSet monitors = resolver.getMonitors(message.getAddress().getLocal());

        // System.out.println("Recipients: "+(monitors ==
        // null?"0":String.valueOf(monitors.size())));
        if (monitors == null || monitors.size() == 0) {
            throw new RecipientNotFoundException(message.getAddress());
        }

        int deletedMonitors = 0;

        monitors.readLock();
        try {
            for (RecipientMonitor monitor : monitors) {
                monitor.deliver(message);
                message.delivered();
            }

            for (RecipientMonitor monitor : monitors) {
                if (monitor.getHandedCounter() >= monitor.getMaxMessages() && monitor.getMaxMessages() > 0) {
                    resolver.removeMonitor(monitor);
                    deletedMonitors++;
                }
            }
        } finally {
            monitors.readUnlock();
        }
        // System.out.println("Send deleted " + deletedSynchs + " synch monitors.") ;

    }

    public void removeRecipient(MessageRecipient recipient, Address address) {
        resolver.removeRecipient(recipient, address);
    }

    @Override
    public void destroy() {
        threadPool.shutdown();
    }
}


class MonitorsSet implements Iterable<RecipientMonitor> {

    private Collection<RecipientMonitor> monits = new LinkedList<RecipientMonitor>();
    ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public Iterator<RecipientMonitor> iterator() {
        return monits.iterator();
    }

    public int size() {
        return monits.size();
    }

    public void readLock() {
        rwLock.readLock().lock();
    }

    public void readUnlock() {
        rwLock.readLock().unlock();
    }

    public void writeLock() {
        rwLock.writeLock().lock();
    }

    public void writeUnlock() {
        rwLock.writeLock().unlock();
    }

    public void add(RecipientMonitor monitor) {
        writeLock();
        monits.add(monitor);
        writeUnlock();
    }
}

interface MonitorAddressResolver {

    public RecipientMonitor addRecipient(MessageRecipient recipient, Address address);

    public RecipientMonitor addSyncRecipient(Address address);

    public void removeMonitor(RecipientMonitor monitor);

    public MonitorsSet getMonitors(String sAddress);

    public void removeRecipient(MessageRecipient recipient, Address address);
}


class HashMonitorAddressResolver implements MonitorAddressResolver {

    private Map<String, MonitorsSet> addressToRecipientMap = Collections
            .synchronizedMap(new TreeMap<String, MonitorsSet>()); //new Hashtable();

    private void addMonitor(RecipientMonitor monitor) {
        String sAddress = monitor.address.getLocal();
        MonitorsSet ms = addressToRecipientMap.get(sAddress);
        if (ms != null) {
            ms.add(monitor);
        } else {
            ms = new MonitorsSet();
            ms.add(monitor);
            addressToRecipientMap.put(sAddress, ms);
        }
    }

    public RecipientMonitor addRecipient(MessageRecipient recipient, Address address) {
        RecipientMonitor monitor = new AsyncRecipientMonitor(recipient);
        monitor.address = address;
        addMonitor(monitor);
        return monitor;
    }

    public RecipientMonitor addSyncRecipient(Address address) {
        RecipientMonitor monitor = new SyncRecipientMonitor();
        monitor.address = address;
        addMonitor(monitor);
        return monitor;
    }

    public void removeMonitor(RecipientMonitor monitor) {
        String sAddress = monitor.address.getLocal();
        addressToRecipientMap.remove(sAddress);
    }

    public MonitorsSet getMonitors(String sAddress) {
//  System.out.println("Dest addr: "+sAddress);

//  for( Object o:addressToRecipientMap.keySet())
//  {
//   System.out.println("Recpt: "+o);
//  }

        return addressToRecipientMap.get(sAddress);
    }

    public void removeRecipient(MessageRecipient recipient, Address address) {
        MonitorsSet monitors = getMonitors(address.getLocal());

        if (monitors == null) {
            return;
        }

        monitors.writeLock();
        try {
            Iterator<RecipientMonitor> iter = monitors.iterator();
            while (iter.hasNext()) {
                if (iter.next().recipient == recipient) {
                    iter.remove();
                    return;
                }
            }
        } finally {
            monitors.writeUnlock();
        }
    }
}

abstract class RecipientMonitor {

    public Message message;
    public Address address;
    public MessageRecipient recipient;
    private int maxMessages = -1;
    private int handedCounter = 0;

    public RecipientMonitor(MessageRecipient recipient) {
        this.recipient = recipient;
    }

    // returns true if the monitor must be deleted from consumers list
    abstract public void deliver(Message message);

    public int getHandedCounter() {
        return handedCounter;
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    public void incHandedCounter() {
        handedCounter++;
    }

    public void setMaxMessages(int i) {
        maxMessages = i;
    }

}

class AsyncRecipientMonitor extends RecipientMonitor {

    public AsyncRecipientMonitor(MessageRecipient recipient) {
        super(recipient);
    }

    public AsyncRecipientMonitor(MessageRecipient recipient, int maxMessages) {
        super(recipient);
        this.setMaxMessages(maxMessages);
    }

    // returns true if the monitor must be deleted from consumers list
    public void deliver(Message message) {
        recipient.receive(message);
        incHandedCounter();
    }
}

class SyncRecipientMonitor extends RecipientMonitor {

    public SyncRecipientMonitor() {
        super(null);
        setMaxMessages(1);
    }

    // returns true if the monitor must be deleted from consumers list
    public void deliver(Message message) {
        synchronized (this) {
            this.message = message;
            this.notify();
        }
        incHandedCounter();
    }
}
