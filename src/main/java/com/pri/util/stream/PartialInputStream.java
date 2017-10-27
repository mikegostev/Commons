package com.pri.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class PartialInputStream extends InputStream {

    private long limit;
    //private long offset;
    private InputStream mainIs;

    public PartialInputStream(InputStream is, long offs, long lim) throws IOException {
//  offset=offs;
        limit = lim;
        mainIs = is;
        is.skip(offs);
    }

    @Override
    public int read() throws IOException {
        if (limit-- > 0) {
            return mainIs.read();
        }

        return -1;
    }

    public long skip(long toskip) throws IOException {
        if (toskip > limit) {
            toskip = limit;
        }

        long n = mainIs.skip(toskip);
        limit -= n;

        return n;
    }
 
 
 /*
 public int read(byte[] arr) throws IOException
 {
  if( limit <= 0 )
   return -1;
  
  int r = mainIs.read( arr );

  if( limit < r )
  {
   int l=(int)limit;
   limit=0;
   return l;
  }
  
  limit-=r;
  
  return r;
 }
*/

    public int read(byte[] arr, int from, int rlen) throws IOException {
        if (limit <= 0) {
            return -1;
        }

        if (rlen > limit) {
            rlen = (int) limit;
        }

        int r = mainIs.read(arr, from, rlen);

//  if( limit < r )
//  {
//   int l=(int)limit;
//   limit=0;
//   return (int)limit;
//  }

        limit -= r;

        return r;
    }

}
