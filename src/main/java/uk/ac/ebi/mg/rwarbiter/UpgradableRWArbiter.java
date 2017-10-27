package uk.ac.ebi.mg.rwarbiter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UpgradableRWArbiter<RT extends Token, WT extends Token, URT extends Token> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition writeReleased = lock.newCondition();
    private final Condition readReleased = lock.newCondition();
    private final Condition uReadReleased = lock.newCondition();

    private int readReqs = 0;
    private Token writeToken = null;
    private Token uReadToken = null;

    private final TokenFactory<RT, WT, URT> factory;

    public UpgradableRWArbiter(TokenFactory<RT, WT, URT> tf) {
        factory = tf;
    }

    public RT getReadLock() {
        try {
            lock.lock();

            while (writeToken != null) {
                writeReleased.awaitUninterruptibly();
            }

            readReqs++;

            return factory.createReadToken();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public URT getUpgradableReadLock() {
        try {
            lock.lock();

            while (uReadToken != null) {
                uReadReleased.awaitUninterruptibly();
            }

            uReadToken = factory.createUpgradableReadToken();

            return (URT) uReadToken;
        } finally {
            lock.unlock();
        }
    }

    public boolean isWriteToken(Token t) {
        return writeToken == t;
    }

    @SuppressWarnings("unchecked")
    public WT getWriteLock() {
        try {
            lock.lock();

            while (uReadToken != null) {
                uReadReleased.awaitUninterruptibly();
            }

            uReadToken = factory.createWriteToken();

            writeToken = uReadToken;

            if (readReqs > 0) {
                readReleased.awaitUninterruptibly();
            }

            return (WT) writeToken;
        } finally {
            lock.unlock();
        }
    }


    @SuppressWarnings("unchecked")
    public WT upgradeReadLock(URT tobj) throws InvalidTokenException {
        try {
            lock.lock();

            if (tobj != uReadToken) {
                throw new InvalidTokenException();
            }

            tobj.setActive(false);

            writeToken = factory.createWriteToken();

            if (readReqs > 0) {
                readReleased.awaitUninterruptibly();
            }

            return (WT) writeToken;
        } finally {
            lock.unlock();
        }
    }

    public void releaseLock(Token tobj) throws InvalidTokenException {
        if (!tobj.isActive()) {
            throw new InvalidTokenException();
        }

        try {
            lock.lock();

            if (writeToken == tobj) {
                writeToken.setActive(false);
                writeToken = null;

                writeReleased.signalAll();
            }

            if (uReadToken == tobj) {
                uReadToken.setActive(false);
                uReadToken = null;

                uReadReleased.signalAll();
            } else {
                tobj.setActive(false);

                readReqs--;

                if (readReqs == 0) {
                    readReleased.signal();
                }
            }
        } finally {
            lock.unlock();
        }

    }


}