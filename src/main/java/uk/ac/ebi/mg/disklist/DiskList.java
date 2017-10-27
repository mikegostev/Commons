package uk.ac.ebi.mg.disklist;

import com.pri.util.collection.ArrayLongList;
import com.pri.util.collection.LongIterator;
import com.pri.util.collection.LongList;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public class DiskList {

    private static final int ptrSize = 5;
    private static final int bufSize = 10000;

    private static final Charset utf8 = Charset.forName("UTF-8");

    private long ptrUp;
    private long ptrDown;

    private long listSize = -1;

    private RandomAccessFile file;

    private final String fname;

    public DiskList(String fname) {
        this.fname = fname;

    }

    public void save(Collection<String> strs) throws IOException {
        file = new RandomAccessFile(fname, "rw");

        LongList ptrLst = new ArrayLongList(100);
        byte[] strBuf = new byte[bufSize];

        ptrUp = 0;
        ptrDown = ptrSize * (strs.size() + 2);

        long lastPtr = ptrDown;

        listSize = strs.size();
        ptrLst.add(listSize);

        int strBufPtr = 0;

        byte[] ptrBuf = null;

        for (String s : strs) {
            byte[] sbyts = s.getBytes(utf8);

            if (strBufPtr == 0 && sbyts.length > strBuf.length) {
                strBuf = new byte[sbyts.length * 2];
            } else if (sbyts.length > (strBuf.length - strBufPtr)) {
                ptrBuf = sink(strBuf, strBufPtr, ptrBuf, ptrLst);
                ptrLst.clear();

                strBufPtr = 0;

                if (sbyts.length > strBuf.length) {
                    strBuf = new byte[sbyts.length * 2];
                }
            }

            ptrLst.add(lastPtr);
            lastPtr += sbyts.length;
            System.arraycopy(sbyts, 0, strBuf, strBufPtr, sbyts.length);
            strBufPtr += sbyts.length;
        }

        ptrLst.add(lastPtr);
        sink(strBuf, strBufPtr, ptrBuf, ptrLst);

        file.close();

        file = null;
    }

    private byte[] sink(byte[] strBuf, int strBufPtr, byte[] ptrBuf, LongList ptrLst) throws IOException {
        int ptrBufPtr = ptrLst.size() * ptrSize;

        if (ptrBuf == null || ptrBuf.length < ptrBufPtr) {
            ptrBuf = new byte[ptrBufPtr + 10 * ptrSize];
        }

        LongIterator ptrItr = ptrLst.listIterator();

        ptrBufPtr = 0;

        while (ptrItr.hasNext()) {
            long ptr = ptrItr.next();

            for (int i = ptrSize - 1; i > 0; i--) {
                ptrBuf[ptrBufPtr++] = (byte) ((ptr >> (8 * i)) & 0xFF);
            }

            ptrBuf[ptrBufPtr++] = (byte) (ptr & 0xFF);
        }

        file.seek(ptrUp);
        file.write(ptrBuf, 0, ptrBufPtr);
        ptrUp += ptrBufPtr;

        file.seek(ptrDown);
        file.write(strBuf, 0, strBufPtr);
        ptrDown += strBufPtr;

        return ptrBuf;
    }

    public List<String> read(int offset, int count) throws IOException {
        int ptrBuftoRead = (count + 1) * ptrSize;

        byte[] readBuf = new byte[bufSize > ptrBuftoRead ? bufSize : ptrBuftoRead];

        if (file == null) {
            file = new RandomAccessFile(fname, "r");

            file.read(readBuf, 0, ptrSize);

            listSize = readBuf[0] & 0xFF;

            for (int i = 1; i < ptrSize; i++) {
                listSize = listSize << 8;
                listSize = listSize | (readBuf[i] & 0xFF);
            }
        }

        if (offset > listSize) {
            throw new NoSuchElementException();
        }

        if (offset + count > listSize) {
            count = (int) (listSize - offset);
            ptrBuftoRead = (count + 1) * ptrSize;
        }

        file.seek((offset + 1) * ptrSize);

        int readSize = 0;

        while (readSize < ptrBuftoRead) {
            int n = file.read(readBuf, readSize, ptrBuftoRead - readSize);

            if (n == -1) {
                throw new NoSuchElementException();
            }

            readSize += n;
        }

        LongList ptrLst = new ArrayLongList(count);

        int ptrBufPtr = 0;

        while (ptrBufPtr < ptrBuftoRead) {
            long ptr = readBuf[ptrBufPtr++] & 0xFF;

            for (int i = 1; i < ptrSize; i++) {
                ptr = ptr << 8;
                ptr = ptr | (readBuf[ptrBufPtr++] & 0xFF);
            }

            ptrLst.add(ptr);
        }

        file.seek(ptrLst.get(0));

        List<String> res = new ArrayList<String>(count);

        int i = 0;
        while (i < count) {
            int strByteLen = (int) (ptrLst.get(i + 1) - ptrLst.get(i));

            if (readBuf.length < strByteLen) {
                readBuf = new byte[strByteLen];
            }

            int toReadLen = strByteLen;

            int moreRd = i + 1;
            while (moreRd < count) {
                strByteLen = (int) (ptrLst.get(moreRd + 1) - ptrLst.get(moreRd));

                if ((toReadLen + strByteLen) < readBuf.length) {
                    toReadLen += strByteLen;
                    moreRd++;
                } else {
                    break;
                }
            }

            readSize = 0;

            while (readSize < toReadLen) {
                int n = file.read(readBuf, readSize, toReadLen - readSize);

                if (n == -1) {
                    throw new IOException("Invalid offset/count parameters");
                }

                readSize += n;
            }

            int nStrs = moreRd - i;
            int sPtr = 0;
            for (int j = 0; j < nStrs; j++) {
                strByteLen = (int) (ptrLst.get(i + j + 1) - ptrLst.get(i + j));

                res.add(new String(readBuf, sPtr, strByteLen, utf8));
                sPtr += strByteLen;
            }

            i = moreRd;
        }

        return res;
    }

    public void close() throws IOException {
        if (file != null) {
            file.close();
        }

        file = null;
    }
}
