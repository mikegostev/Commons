package uk.ac.ebi.mg.rwarbiter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RWArbiter<RT extends Token, WT extends Token, URT extends Token> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition writeReleased = lock.newCondition();
    private final Condition readReleased = lock.newCondition();

    private int readReqs = 0;
    private Token writeToken = null;

    private final TokenFactory<RT, WT, URT> factory;

    public RWArbiter(TokenFactory<RT, WT, URT> tf) {
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

    public boolean isWriteToken(Token t) {
        return writeToken == t;
    }

    @SuppressWarnings("unchecked")
    public WT getWriteLock() {
        try {
            lock.lock();

            while (writeToken != null) {
                writeReleased.awaitUninterruptibly();
            }

            writeToken = factory.createWriteToken();

            if (readReqs > 0) {
                readReleased.awaitUninterruptibly();
            }

            return (WT) writeToken;
        } finally {
            lock.unlock();
        }
    }

// public boolean checkTokenValid( Object tobj )
// {
//  return  tobj instanceof ReadWriteToken && ((ReadWriteToken)tobj).isActive();
// }

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
