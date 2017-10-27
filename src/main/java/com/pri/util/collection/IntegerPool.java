package com.pri.util.collection;

import com.pri.util.Interval;
import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;

public class IntegerPool implements Serializable {

    private int ver = 0;
    private IntList vals;
    private Collection<Interval> ivals;

    public IntegerPool() {
    }

    public void addValue(Integer v) {
        addValue(v.intValue());
    }

    public void addValue(int v) {
        if (vals == null) {
            vals = new ArrayIntList(10);
        }

        if (ivals != null) {
            for (Interval ivl : ivals) {
                if (ivl.contains(v)) {
                    return;
                }
            }
        }

        ver++;
        if (!vals.contains(v)) {
            vals.add(v);
        }
    }

    public void addInterval(Interval iv) {
        ver++;

        if (ivals == null) {
            ivals = new LinkedList<Interval>();
        } else {
            for (Interval ivl : ivals) {
                if (ivl.contains(iv.getBegin()) || iv.contains(ivl.getBegin())) {
                    ivl.setBegin(iv.getBegin() < ivl.getBegin() ? iv.getBegin() : ivl.getBegin());
                    ivl.setEnd(iv.getEnd() > ivl.getBegin() ? iv.getEnd() : ivl.getEnd());
                    return;
                }
            }
        }

        ivals.add(iv);

        if (vals != null) {
            IntListIterator itr = vals.listIterator();

            while (itr.hasNext()) {
                if (iv.contains(itr.next())) {
                    itr.remove();
                }
            }
        }
    }

    public int countSingles() {
        if (vals == null) {
            return 0;
        }

        return vals.size();
    }

    public int countIntervals() {
        if (ivals == null) {
            return 0;
        }

        return ivals.size();
    }

    public Collection<Interval> getIntervals() {
        return ivals;
    }

    public IntList getValues() {
        return vals;
    }

    public IntIterator iterator() {
        return new IvIterator();
    }

    private class IvIterator implements IntIterator {

        int iver;
        Iterator<Interval> ivalIter;
        IntIterator valIter;

        boolean ivalsDone = false;
        boolean valsDone = false;
        Interval cIval = null;
        int cVal;

        IvIterator() {
            iver = ver;

            if (ivals != null) {
                ivalIter = ivals.iterator();

                if (ivalIter.hasNext()) {
                    cIval = ivalIter.next();
                    cVal = cIval.getBegin();
                } else {
                    ivalIter = null;
                }
            }

            if (vals != null) {
                valIter = vals.listIterator();
            }
        }

        public boolean hasNext() {
            if (iver != ver) {
                throw new ConcurrentModificationException();
            }

            if (ivalIter != null || (valIter != null && valIter.hasNext())) {
                return true;
            }

            return false;
        }

        public int next() {
            if (iver != ver) {
                throw new ConcurrentModificationException();
            }

            if (ivalIter != null) {
                int val = cVal;

                cVal++;

                if (cVal > cIval.getEnd()) {
                    if (ivalIter.hasNext()) {
                        cIval = ivalIter.next();
                        cVal = cIval.getBegin();
                    } else {
                        ivalIter = null;
                    }
                }

                return val;
            }

            if (valIter != null && valIter.hasNext()) {
                return valIter.next();
            } else {
                valIter = null;
            }

            throw new ArrayIndexOutOfBoundsException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
