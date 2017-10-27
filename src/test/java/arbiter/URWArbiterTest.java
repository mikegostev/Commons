package arbiter;

import java.util.ArrayList;
import java.util.Collection;
import uk.ac.ebi.mg.rwarbiter.InvalidTokenException;
import uk.ac.ebi.mg.rwarbiter.Token;
import uk.ac.ebi.mg.rwarbiter.TokenFactory;
import uk.ac.ebi.mg.rwarbiter.UpgradableRWArbiter;

public class URWArbiterTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Service svc = new Service();

        Collection<Runner> rcoll = new ArrayList<URWArbiterTest.Runner>();

        for (int i = 0; i < 5; i++) {
            WWorker ww = new WWorker(svc);
            rcoll.add(ww);
            ww.start();
        }

        for (int i = 0; i < 5; i++) {
            URWorker ww = new URWorker(svc);
            rcoll.add(ww);
            ww.start();
        }

        for (int i = 0; i < 20; i++) {
            RWorker ww = new RWorker(svc);
            rcoll.add(ww);
            ww.start();
        }

        new WDog(svc, rcoll).start();

    }

    static class Request extends Token {

        boolean isWrite;
        boolean isURead;
        boolean isRead;

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean isRead) {
            this.isRead = isRead;
        }

        public boolean isWrite() {
            return isWrite;
        }

        public void setWrite(boolean isWrite) {
            this.isWrite = isWrite;
        }

        public boolean isURead() {
            return isURead;
        }

        public void setURead(boolean isURead) {
            this.isURead = isURead;
        }

    }

    static class WDog extends Thread {

        Service svc;
        Collection<Runner> runners;

        public WDog(Service s, Collection<Runner> runs) {
            setName("WDog");
            svc = s;
            runners = runs;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(30000);
                } catch (InterruptedException e) {
                }

                svc.printStat();

                long cTime = System.currentTimeMillis();

                for (Runner r : runners) {
                    if ((cTime - r.getLastEventTS()) > 5000) {
                        System.out.println(r.getClass().getName() + " locked");
                    }
                }

            }
        }
    }

    interface Runner {

        long getLastEventTS();
    }

    static class Service {

        UpgradableRWArbiter<Request, Request, Request> arb = new UpgradableRWArbiter<Request, Request, Request>(
                new TokenFactory<Request, Request, Request>() {

                    @Override
                    public Request createReadToken() {
                        Request req = new Request();

                        req.setRead(true);
                        req.setWrite(false);
                        req.setURead(false);

                        return req;
                    }

                    @Override
                    public Request createWriteToken() {
                        Request req = new Request();

                        req.setRead(false);
                        req.setWrite(true);
                        req.setURead(false);

                        return req;
                    }

                    @Override
                    public Request createUpgradableReadToken() {
                        Request req = new Request();

                        req.setRead(false);
                        req.setWrite(false);
                        req.setURead(true);

                        return req;
                    }

                });

        long requests;
        long readRequests;
        long writeRequests;
        long uReadRequests;
        long uWriteRequests;

        int maxSimRReqs;
        int maxSimRReqsWithUR;
        int maxSimRReqsWithW;
        int maxSimRReqsWithUW;
        int maxSimWReqs;
        int maxSimExReqs;
        int maxSimURReqs;
        int maxSimUWReqs;

        int cRead;
        int cWrite;
        int cURead;
        int cUWrite;

        synchronized void printStat() {
            System.out.println("Requests: " + requests);
            System.out.println("Read requests: " + readRequests);
            System.out.println("Write requests: " + writeRequests);
            System.out.println("Upgradable read requests: " + uReadRequests);
            System.out.println("Upgraded read to write requests: " + uWriteRequests);
            System.out.println("Max simultaneous read requests : " + maxSimRReqs);
            System.out.println(
                    "Max simultaneous read requests while some upgradable read requests  : " + maxSimRReqsWithUR);
            System.out.println("Max simultaneous read requests while some write requests  : " + maxSimRReqsWithW);
            System.out.println(
                    "Max simultaneous read requests while some upgraded write requests  : " + maxSimRReqsWithUW);
            System.out.println("Max simultaneous write requests  : " + maxSimWReqs);
            System.out.println("Max simultaneous exclusive requests  : " + maxSimExReqs);
            System.out.println("Max simultaneous upgradable read requests  : " + maxSimURReqs);
            System.out.println("Max simultaneous upgraded read to write requests  : " + maxSimURReqs);

        }

        Request getReadLock() {
            return arb.getReadLock();
        }

        Request getUReadLock() {
            return arb.getUpgradableReadLock();
        }

        Request getWriteLock() {
            return arb.getWriteLock();
        }

        void upgradeReadLock(Request r) throws InvalidTokenException {
            arb.upgradeReadLock(r);

            r.setWrite(true);
        }

        void unlock(Request r) throws InvalidTokenException {
            arb.releaseLock(r);
        }

        void service(Request tok) {
            synchronized (this) {
                requests++;

                if (tok.isRead()) {
                    readRequests++;
                    cRead++;

                    if (cRead > maxSimRReqs) {
                        maxSimRReqs = cRead;
                    }

                    if (cWrite > 0 && cRead > maxSimRReqsWithW) {
                        maxSimRReqsWithW = cRead;
                    }

                    if (cURead > 0 && cRead > maxSimRReqsWithUR) {
                        maxSimRReqsWithUR = cRead;
                    }

                    if (cUWrite > 0 && cRead > maxSimRReqsWithUW) {
                        maxSimRReqsWithUW = cRead;
                    }
                } else if (tok.isWrite()) {
                    if (tok.isURead()) {
                        uWriteRequests++;
                        cUWrite++;

                        if (cUWrite > maxSimUWReqs) {
                            maxSimUWReqs = cUWrite;
                        }
                    } else {
                        writeRequests++;
                        cWrite++;

                        if (cWrite > maxSimWReqs) {
                            maxSimWReqs = cWrite;
                        }
                    }

                    if ((cWrite + cUWrite) > maxSimExReqs) {
                        maxSimExReqs = cWrite + cUWrite;
                    }
                } else if (tok.isURead()) {
                    uReadRequests++;
                    cURead++;

                    if (cURead > maxSimURReqs) {
                        maxSimURReqs = cURead;
                    }
                }

            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (this) {
                if (tok.isRead()) {
                    cRead--;
                } else if (tok.isWrite()) {
                    if (tok.isURead()) {
                        cUWrite--;
                    } else {
                        cWrite--;
                    }
                } else if (tok.isURead()) {
                    cURead--;
                }
            }

        }
    }

    static class RWorker extends Thread implements Runner {

        Service svc;
        long ts;

        public RWorker(Service s) {
            setName("RWorker");
            svc = s;
        }

        @Override
        public void run() {
            while (true) {
                Request req = svc.getReadLock();

                svc.service(req);

                try {
                    svc.unlock(req);
                } catch (InvalidTokenException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }

                ts = System.currentTimeMillis();
            }

        }

        @Override
        public long getLastEventTS() {
            return ts;
        }
    }

    static class WWorker extends Thread implements Runner {

        Service svc;
        long ts;

        public WWorker(Service s) {
            setName("WWorker");
            svc = s;
        }

        @Override
        public void run() {
            while (true) {
                Request req = svc.getWriteLock();

                svc.service(req);

                try {
                    svc.unlock(req);
                } catch (InvalidTokenException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }

                ts = System.currentTimeMillis();
            }

        }

        @Override
        public long getLastEventTS() {
            return ts;
        }

    }

    static class URWorker extends Thread implements Runner {

        Service svc;
        long ts;

        public URWorker(Service s) {
            setName("URWorker");
            svc = s;
        }

        @Override
        public void run() {
            while (true) {
                Request req = svc.getUReadLock();

                svc.service(req);

                try {
                    svc.upgradeReadLock(req);
                } catch (InvalidTokenException e1) {
                    e1.printStackTrace();
                }

                svc.service(req);

                try {
                    svc.unlock(req);
                } catch (InvalidTokenException e) {
                    e.printStackTrace();
                }

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }

                ts = System.currentTimeMillis();
            }

        }

        @Override
        public long getLastEventTS() {
            return ts;
        }

    }

}
