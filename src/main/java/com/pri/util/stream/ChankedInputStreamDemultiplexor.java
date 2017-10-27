/*
 * Created on 03.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ChankedInputStreamDemultiplexor implements Iterator<InputStream> {

    private InputStream mis;

    private static final int BUFFER_SIZE = 30000;

    byte[] buf = new byte[BUFFER_SIZE];
    int begPtr = 0, endPtr = 0;

    int chunkSize = 0;
    int chunkRead = 0;

    private boolean endStream = false;

    private InputStreamUnchunker currUnck;
    private boolean nextReady = false;

    /**
     *
     */
    public ChankedInputStreamDemultiplexor(InputStream is) {
        mis = is;
    }


    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (endStream) {
            return false;
        }

        if (nextReady) {
            return true;
        }

        if (currUnck != null) {
            try {
                currUnck.detach();
            } catch (IOException e) {
                e.printStackTrace();
                endStream = true;
                return false;
            }
        }

        try {
            if (fillBuffer()) {
                currUnck = new InputStreamUnchunker(this);
                nextReady = true;
            } else {
                endStream = true;
                return false;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            endStream = true;
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public InputStream next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        ;

        nextReady = false;
        return currUnck;
    }

    boolean fillBuffer() throws IOException {
        if (chunkRead == chunkSize && begPtr >= endPtr) {
            return readNewChunk();
        }

        int r;

        if (chunkSize - chunkRead > BUFFER_SIZE) {
            r = BUFFER_SIZE;
        } else {
            r = chunkSize - chunkRead;
        }

        r = mis.read(buf, 0, r);

        if (r == -1) {
            throw new IOException("Invalid chunked stream");
        }

        chunkRead += r;

        begPtr = 0;
        endPtr = r;

        return true;

    }

    private boolean readNewChunk() throws IOException {
        int c;

        c = mis.read();

        if (c == -1) {

            mis.close();
            endStream = true;
            return false;
        }

        if (c != '\n') {
            throw new IOException("Invalid chunked stream");
        }

        c = mis.read();

        chunkSize = 0;

        while (c != -1 && c != '\n') {
            if (c >= '0' && c <= '9') {
                chunkSize = chunkSize << 4;
                chunkSize = chunkSize | (c - '0');
            } else if (c >= 'A' && c <= 'F') {
                chunkSize = chunkSize << 4;
                chunkSize = chunkSize | (c - 'A' + 10);
            } else if (c >= 'a' && c <= 'f') {
                chunkSize = chunkSize << 4;
                chunkSize = chunkSize | (c - 'a' + 10);
            } else if (c == '$') {
                endStream = true;
                mis.close();
                return false;
            } else {
                throw new IOException("Invalid chunked stream");
            }

            c = mis.read();

        }

        if (c == -1) {
            throw new IOException("Invalid chunked stream");
        }

        if (chunkSize == 0) {
            chunkRead = 0;
            return false;
        }

        if (BUFFER_SIZE < chunkSize) {
            chunkRead = BUFFER_SIZE;
        } else {
            chunkRead = chunkSize;
        }

        chunkRead = mis.read(buf, 0, chunkRead);

        begPtr = 0;
        endPtr = chunkRead;

        return true;
    }

}


class InputStreamUnchunker extends InputStream {

    private ChankedInputStreamDemultiplexor masterISD;
    private byte[] buf;
    int bufPtr = 0;


    public InputStreamUnchunker(ChankedInputStreamDemultiplexor is) {
        masterISD = is;
    }
 /* (non-Javadoc)
  * @see java.io.InputStream#read()
  */

    public int read(byte b[], int off, int len) throws IOException {
        if (buf != null) {
            if (bufPtr >= buf.length) {
                masterISD = null;
                buf = null;
                return -1;
            }

            if (buf.length - bufPtr < len) {
                len = buf.length - bufPtr;
            }

            for (int i = 0; i < len; i++) {
                b[off++] = buf[bufPtr++];
            }

            return len;
        }

        if (masterISD == null) {
            return -1;
        }

        if (masterISD.begPtr >= masterISD.endPtr) {
            if (!masterISD.fillBuffer()) {
                masterISD = null;
                return -1;
            }
        }

        if (masterISD.begPtr >= masterISD.endPtr) {
            masterISD = null;
            return -1;
        }

        if (masterISD.endPtr - masterISD.begPtr < len) {
            len = masterISD.endPtr - masterISD.begPtr;
        }

        for (int i = 0; i < len; i++) {
            b[off++] = masterISD.buf[masterISD.begPtr++];
        }

        return len;

    }

    /* public int read(byte b[], int off, int len) throws IOException
     {
      if (b == null)
      {
       throw new NullPointerException();
      }
      else if ((off < 0) || (off > b.length) || (len < 0)
        || ((off + len) > b.length) || ((off + len) < 0))
      {
       throw new IndexOutOfBoundsException();
      }
      else if (len == 0) { return 0; }

      int c = read();
      if (c == -1) { return -1; }
      b[off] = (byte) c;
    //System.out.println(c);
      int i = 1;
      try
      {
       for (; i < len; i++)
       {
        c = read();
        if (c == -1)
        {
         break;
        }
        if (b != null)
        {
         b[off + i] = (byte) c;
    //     System.out.println(c);
        }
       }
      }
      catch (IOException ee)
      {
      }
      return i;
     }
     */
    public int read() throws IOException {
        if (buf != null) {
            if (bufPtr >= buf.length) {
                masterISD = null;
                buf = null;
                return -1;
            }

            return buf[bufPtr++];
        }

        if (masterISD == null) {
            return -1;
        }

        if (masterISD.begPtr >= masterISD.endPtr) {
            if (!masterISD.fillBuffer()) {
                masterISD = null;
                return -1;
            }
        }

        if (masterISD.begPtr >= masterISD.endPtr) {
            masterISD = null;
            return -1;
        }

        return (masterISD.buf[masterISD.begPtr++]) & 0xFF;
    }

    void detach() throws IOException {
        if (masterISD == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (StreamPump.doPump(this, baos) > 0) {
            buf = baos.toByteArray();
        }

        masterISD = null;
  
/*  if( masterISD.chunkRead >= masterISD.chunkSize )
  {
   masterISD=null;
   return;
  }
  
  buf = new byte[masterISD.chunkSize-masterISD.chunkRead];

  int ofs=0;
  
  while( ofs < buf.length )
  {
   ofs+=read(buf,ofs,buf.length-ofs); 
  }
 */
    }

}

