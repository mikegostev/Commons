package com.pri.messenger;

import com.pri.log.Log;
import com.pri.util.ErrorInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ProxyUtils {

    // private static final String SEP1="~~~";
// private static final String SEP2="@@@";
    private static final String EXCEPTION_CONTENT_TYPE = "application/shexexception";

// public static Throwable unpackExceptionOld( String message )
// {
//  int offs=0;
//  Throwable deriv=null;
//  do
//  {
//   int pos = message.indexOf(SEP1,offs);
//   
//   String excStr=null;
//   if( pos == -1 )
//   {
//    excStr=message.substring(offs);
//    offs = -1;
//   }
//   else
//   {
//    excStr=message.substring(offs,pos);
//    offs=pos+SEP1.length();
//   }
//   
//   pos = excStr.indexOf(SEP1);
//   String part = "";
//   
//  }while( offs > 0 );
//  
//  
//  
//  String[] parts = message.split("~~~");
//
//  try
//  {
//   String [] msgCl = parts[0].split("@@@");
//   
//   Constructor c = Class.forName(msgCl[1]).getConstructor( new Class[]{ String.class } );
//   Throwable t = (Throwable)c.newInstance(  new Object[]{msgCl[0]} );
//   
//   Throwable e=t;
//   
//   for(int i=1; i < parts.length; i++ )
//   {
//    msgCl = parts[i].split("@@@");
//    
//    Throwable eObj;
//    try
//    {
//     Constructor ctor = Class.forName(msgCl[1]).getConstructor( new Class[]{ String.class }  );
//     eObj = (Throwable)ctor.newInstance( new Object[]{msgCl[0]} );
//    }
//    catch( ClassNotFoundException cnfE )
//    {
//     eObj =  new Exception(msgCl[0]);
//    }
//
//    e.initCause( eObj );
//    e=eObj;
//  }
//   
//   return t;
//  }
//  catch (Exception e)
//  {
//   return null;
//  }
// }
//
// 
// public static <T extends Throwable> T unpackExceptionOld( String message, Class<T> exc )
// {
//  String[] parts = message.split("~~~");
//
//  try
//  {
//   String [] msgCl = parts[0].split("@@@");
//   
//   Constructor<T> c = exc.getConstructor( new Class[]{ String.class } );
//   T t = c.newInstance( 
//     new Object[]{msgCl[0]} );
//   
//   Throwable e=t;
//   
//   for(int i=1; i < parts.length; i++ )
//   {
//    msgCl = parts[i].split("@@@");
//    
//    Throwable eObj;
//    try
//    {
//     Constructor ctor = Class.forName(msgCl[1]).getConstructor( new Class[]{ String.class }  );
//     eObj = (Throwable)ctor.newInstance( 
//       new Object[]{msgCl[0]} );
//    }
//    catch( ClassNotFoundException cnfE )
//    {
//     eObj =  new Exception(msgCl[0]);
//    }
//
//    e.initCause( eObj );
//    e=eObj;
//  }
//   
//   return t;
//  }
//  catch (Exception e)
//  {
//   return null;
//  }
// }
//
// public static String packExceptionOld( Throwable e )
// {
//  StringBuffer sb = new StringBuffer();
//
//  do
//  {
//   sb.append(e.toString());
//   sb.append("@@@").append(e.getClass().getName());
//   
//   if( e.getCause() != null )
//   {
//    sb.append("~~~");
//    e=e.getCause();
//   }
//   else
//    break;
//  }
//  while( true );
//  
//  return sb.toString();
// }

    public static MessageBody packException(Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            while (true) {
                ExceptionInfo ei = new ExceptionInfo();
                ei.setMessage(e.getMessage());
                ei.setStackTrace(e.getStackTrace());
                ei.setExceptionClass(e.getClass().getName());

                if (e instanceof ErrorInfo) {
                    ei.setErrCode(((ErrorInfo) e).getErrorCode());
                    ei.setAuxInfo(((ErrorInfo) e).getAuxInfo());
                }

                oos.writeObject(ei);

                if (e.getCause() == null || e == e.getCause()) {
                    break;
                }

                e = e.getCause();
            }

            oos.close();
        } catch (IOException ioe) {
            Log.error("Can't serialize ExceptionInfo", ioe);
        }

        return new ByteArrayADOBMessageBody(baos.toByteArray(), EXCEPTION_CONTENT_TYPE);
    }

    public static void checkException(MessageBody mb) throws Throwable {
        if (!(mb instanceof ADOBMessageBody) || !EXCEPTION_CONTENT_TYPE
                .equalsIgnoreCase(((ADOBMessageBody) mb).getContentType())) {
            return;
        }

        Throwable t = null;

        try {
            t = unpackException(((ADOBMessageBody) mb).getContent());
        } catch (IOException e) {
            Log.error("IO error while receiving exception content", e);
        }

        throw t;
    }

    public static Throwable unpackException(byte[] data) {
        Throwable top = null, curr = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);

            while (true) {
                ExceptionInfo ei = (ExceptionInfo) ois.readObject();

                Throwable t = null;
                try {
                    Class<?> extClass = Class.forName(ei.getExceptionClass());

                    if (ErrorInfo.class.isAssignableFrom(extClass)) {
                        try {
                            t = (Throwable) extClass.getConstructor(new Class[]{String.class, int.class, Object.class})
                                    .newInstance(ei.getMessage(), ei.getErrCode(), ei.getAuxInfo());
                        } catch (Throwable e) {
                        }
                    }

                    if (t == null) {
                        t = (Throwable) extClass.getConstructor(new Class[]{String.class}).newInstance(ei.getMessage());
                    }
                } catch (ClassNotFoundException e) {
                    t = new Exception(
                            "Unavailable class (" + ei.getExceptionClass() + ") exception: " + ei.getMessage());
                } catch (Exception e) {
                    t = new Exception(
                            "Unavailable class (" + ei.getExceptionClass() + ") exception (init error: '" + e + "'): "
                                    + ei.getMessage());
                }

                if (curr == null) {
                    top = t;
                    curr = t;
                } else {
                    curr.initCause(t);
                    curr = t;
                }

                t.setStackTrace(ei.getStackTrace());
            }

        } catch (EOFException e) {
        } catch (IOException e) {
            Log.warn("IO error while unpacking exception", e);
        } catch (ClassNotFoundException e) {
            Log.error("Oops! Impossible!", e);
        }

        return top;
    }


    static class ExceptionInfo implements Serializable {

        private String message;
        private int errCode;
        private Object auxInfo;
        private StackTraceElement[] stTrace;
        private String exceptionClass;

        public Object getAuxInfo() {
            return auxInfo;
        }

        public void setAuxInfo(Object auxInfo) {
            this.auxInfo = auxInfo;
        }

        public int getErrCode() {
            return errCode;
        }

        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public StackTraceElement[] getStackTrace() {
            return stTrace;
        }

        public void setStackTrace(StackTraceElement[] stTrace) {
            this.stTrace = stTrace;
        }

        public String getExceptionClass() {
            return exceptionClass;
        }

        public void setExceptionClass(String exceptionClass) {
            this.exceptionClass = exceptionClass;
        }
    }
}
