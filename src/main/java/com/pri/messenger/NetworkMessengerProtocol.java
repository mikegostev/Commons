/*
 * Created on 27.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.adob.ADOBFactory;
import com.pri.log.Log;
import com.pri.log.Logger;
import com.pri.util.LightSAXParser;
import com.pri.util.M1codec;
import com.pri.util.SimpleTokenizer;
import com.pri.util.StringPair;
import com.pri.util.stream.ChankedInputStreamDemultiplexor;
import com.pri.util.stream.InputStreamMultiplexor;
import com.pri.util.stream.SequentialInputStreamDemultiplexor;
import com.pri.util.stream.StreamPump;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */

public class NetworkMessengerProtocol {

    public enum ReqType {
        INVALID, POLL, SYNCREQUEST, SYNCREPLY, ASYNCREQUEST, ASYNCREPLY, EXCEPTION, IDENTIFYREQ, IDENTIFYRESP,
        SESSEXPIRED, CLOSE, CLOSEACK, NOP
    }

    static final Logger logger = Log.getLogger(NetworkMessengerProtocol.class);
 
 /*
 final static String             TYPE_POLL               = "POLL";
 final static String             TYPE_ASYNCREQUEST       = "ASYNCREQUEST";
 final static String             TYPE_SYNCREQUEST        = "SYNCREQUEST";
 final static String             TYPE_SYNCREPLY          = "SYNCREPLY";
 final static String             TYPE_ASYNCREPLY         = "ASYNCREPLY";
 final static String             TYPE_EXCEPTION          = "EXCEPTION";
 final static String             TYPE_IDENTIFY           = "IDENTIFY";
 final static String             TYPE_SESSEXPIRED        = "SESSEXPIRED";
*/

    final static String STREAM_CONTENT_TYPE = "message/stream";
    final static String SERIALIZED_CONTENT_TYPE = "message/serialized";
    final static String COMPOSITE_CONTENT_TYPE = "message/composite";
    final static String XML_CONTENT_TYPE = "text/xml";

    final static String X_MSG_MSGTYPE = "X-MSG-MSGTYPE";
    final static String X_MSG_ID = "X-MSG-ID";
    final static String X_MSG_TYPE = "X-MSG-TYPE";
    final static String X_MSG_POLL_LIFETIME = "X-MSG-POLL-LIFETIME";
    final static String X_MSG_HANDINGS = "X-MSG-HANDINGS";
    final static String X_MSG_ADDRESS = "X-MSG-ADDRESS";
    final static String X_MSG_CLASS = "X-MSG-CLASS";
    final static String X_MSG_CONTENT = "X-MSG-CONTENT";
    final static String X_MSG_SIZES = "X-MSG-SIZES";
    final static String X_MSG_ADOB_ContentType = "X-MSG-ADOB-ContentType";
    final static String X_MSG_ADOB_ContentID = "X-MSG-ADOB-ContentID";
    final static String X_MSG_ADOB_ContentDisposition = "X-MSG-ADOB-ContentDisposition";

    public final static String X_MSG_VER = "X-MSG-VER";
    public final static String X_MSG_QLEN = "X-MSG-QLEN";

// static String                   POLL_MESSAGE            = "<MESSAGE><HEADER TYPE=\"POLL\"/></MESSAGE>";

    static Class<?> autoRestoreMBClass;
    static Class<?> xmlSerializableMBClass;
    static Class<?> autoParserMBClass;
    static Class<?> beanMBClass;

    private static SAXParserFactory parserFactory;

    private static Options options;

    public static class Options {

        int maxMessageSize = 2000000;

        public int getMaxMessageSize() {
            return maxMessageSize;
        }

        public void setMaxMessageSize(int maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
        }
    }

    static {
        try {
            String myClassName = NetworkMessengerProtocol.class.getName();
            String myPackageName = myClassName.substring(0, myClassName.lastIndexOf('.') + 1);

            xmlSerializableMBClass = Class.forName(myPackageName + "XMLSerializableMessageBody");

            autoRestoreMBClass = Class.forName(myPackageName + "AutoRestoreMessageBody");

            autoParserMBClass = Class.forName(myPackageName + "AutoParserMessageBody");

            beanMBClass = Class.forName(myPackageName + "BeanMessageBody");
        } catch (ClassNotFoundException e) {
            Log.error("Class not found", e);
        }

        parserFactory = SAXParserFactory.newInstance();

        options = new Options();
    }

    static public TransferUnit prepareAsyncRequest(Message m) throws IOException {
        return prepareRequest(m, ReqType.ASYNCREQUEST);
    }

    static public TransferUnit prepareSyncRequest(Message m) throws IOException {
        return prepareRequest(m, ReqType.SYNCREQUEST);
    }

    static public TransferUnit prepareRequest(Message m, ReqType type) throws IOException {
//  MessageBody body = m.getBody();
        TransferUnit tu = new TransferUnit();
        tu.setProperty(X_MSG_TYPE, type.name());
        tu.setProperty(X_MSG_ADDRESS, m.getAddress().toString());

        if (m.getType() != null) {
            tu.setProperty(X_MSG_MSGTYPE, m.getType());
        }

        return prepareTU(tu, m.getBodies(), type, m.getAddress(), m.getID(), 0);
 
  /*
  
  if (body instanceof ADOBMessageBody)
  {
   tu.setContentType(STREAM_CONTENT_TYPE);
   tu.setProperty(X_MSG_TYPE, type.name() );
   tu.setProperty(X_MSG_ADDRESS, m.getAddress().toString());
   tu.setProperty(X_MSG_CLASS, body.getClass().getName());
   tu.setProperty(X_MSG_ADOB_ContentType, ((ADOBMessageBody)body).getContentType());
   
   String str;
   str = ((ADOBMessageBody)body).getMimeDisposition();
   if( str != null )
    tu.setProperty(X_MSG_ADOB_ContentDisposition, str);

   str = ((ADOBMessageBody)body).getContentID();
   if( str != null )
    tu.setProperty(X_MSG_ADOB_ContentID, str);

   if (m.getID() != null)
    tu.setProperty(X_MSG_ID, m.getID());

   int sz = (int) ((ADOBMessageBody) body).getContentSize();
   
   if( sz > 0 )
    tu.setContentSize( sz );
   
   tu.setContentInputStream(((ADOBMessageBody) body).getInputStream());

  }
  else if (body instanceof SerializableMessageBody)
  {
   tu.setContentType(SERIALIZED_CONTENT_TYPE);
   tu.setProperty(X_MSG_TYPE, type.name() );
   tu.setProperty(X_MSG_ADDRESS, m.getAddress().toString());

   if (m.getID() != null)
    tu.setProperty(X_MSG_ID, m.getID());

   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   ObjectOutputStream e = new ObjectOutputStream(baos);
   e.writeObject(body);
   e.close();

   byte[] barr = baos.toByteArray();
   tu.setContentSize(barr.length);

   tu.setContentInputStream(new ByteArrayInputStream(barr));

  }
  else if (body instanceof XMLableMessageBody)
  {
   tu.setContentType(XML_CONTENT_TYPE);

   StringBuffer sb = new StringBuffer();

   sb.append("<MESSAGE><HEADER TYPE=\"");
   sb.append(type);
   sb.append("\" ADDRESS=\"");
   sb.append(m.getAddress().toString());

   if (m.getType() != null)
   {
    sb.append("\" MSGTYPE=\"");
    sb.append(m.getType());
   }

   if (m.getID() != null)
   {
    sb.append("\" ID=\"");
    sb.append(m.getID());
   }

   sb.append("\" />");

   if (body != null)
   {
    sb.append("<BODY CLASS=\"" + body.getClass().getName() + "\">"
      + ((XMLableMessageBody)body).toXML() + "</BODY>");
   }

   sb.append("</MESSAGE>");

   try
   {
    byte[] barr = sb.toString().getBytes("UTF-8");
    tu.setContentSize( barr.length );

    tu.setContentInputStream(new ByteArrayInputStream( barr ));
   }
   catch (UnsupportedEncodingException e)
   {
    Log.error("Unsupported UTF-8? Impossible!",e);
   }
  }

  return tu;
  */
    }

    static public TransferUnit prepareIdentifyRequest() {
        return prepareSimpleRequest(ReqType.IDENTIFYREQ);
    }

    static public TransferUnit prepareSessionExpired() {
        return prepareSimpleRequest(ReqType.SESSEXPIRED);
    }

    static public TransferUnit prepareIdentifyReply(String addr, int lifeTime) {
        TransferUnit tu = new TransferUnit();

        tu.setContentType(STREAM_CONTENT_TYPE);
        tu.setProperty(X_MSG_POLL_LIFETIME, String.valueOf(lifeTime));
        tu.setProperty(X_MSG_ADDRESS, addr);
        tu.setProperty(X_MSG_TYPE, ReqType.IDENTIFYRESP.name());

        return tu;

//
//  TransferUnit tu = new TransferUnit();
//  tu.setContentType(XML_CONTENT_TYPE);
//  tu.setProperty(X_MSG_POLL_LIFETIME,String.valueOf(lifeTime));
//
//  StringBuffer reply = new StringBuffer();
//
//  reply.append("<MESSAGE><HEADER TYPE=\"");
//  reply.append(ReqType.IDENTIFY.name());
//  reply.append("\" ADDRESS=\"");
//  reply.append(addr);
//  reply.append("\"/></MESSAGE>");
//
//  try
//  {
//   tu.setContentInputStream(new ByteArrayInputStream(reply.toString().getBytes(
//     "UTF-8")));
//  }
//  catch (UnsupportedEncodingException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
//
//  return tu;
    }


    static private TransferUnit prepareTU(TransferUnit tu, List<MessageBody> bodies, ReqType rType, Address addr,
            String msgID, int handings) throws IOException {
        boolean hasBin = false;
        int sizesKnown = 0;

        StringBuilder bodyTypes = null;
        StringBuilder bodySizes = null;

        StringBuilder adobTypes = null;
        StringBuilder adobIDs = null;
        StringBuilder adobDisps = null;

        int nResp = 0;

        List<InputStream> islist = new LinkedList<InputStream>();

        if (bodies != null) {
            nResp = bodies.size();
        }

        if (nResp != 0) {
            bodyTypes = new StringBuilder();
            bodySizes = new StringBuilder();

            int sz;

            for (MessageBody rB : bodies) {
                if (rB instanceof ADOBMessageBody) {
                    ADOBMessageBody amb = (ADOBMessageBody) rB;

                    hasBin = true;
                    bodyTypes.append(",stream");

                    islist.add(amb.getInputStream());

                    sz = (int) amb.getContentSize();

                    if (sizesKnown != -1 && sz > 0) {
                        bodySizes.append(',').append(sz);
                        sizesKnown += sz;
                    } else {
                        sizesKnown = -1;
                        bodySizes = null;
                    }

                    if (adobTypes == null) {
                        adobTypes = new StringBuilder();
                    }

                    if (adobIDs == null) {
                        adobIDs = new StringBuilder();
                    }

                    if (adobDisps == null) {
                        adobDisps = new StringBuilder();
                    }

                    adobTypes.append(M1codec.encode(amb.getContentType(), ",")).append(',');

                    if (amb.getContentID() != null) {
                        adobIDs.append(M1codec.encode(amb.getContentID(), ","));
                    }

                    adobIDs.append(',');

                    if (amb.getDisposition() != null) {
                        adobDisps.append(M1codec.encode(amb.getDisposition(), ","));
                    }

                    adobDisps.append(',');
                } else if (rB instanceof SerializableMessageBody) {
                    hasBin = true;
                    bodyTypes.append(",serialized");

//     try
//     {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream e = new ObjectOutputStream(baos);
                    e.writeObject(rB);
                    e.close();

                    byte[] bytarr = baos.toByteArray();

                    islist.add(new ByteArrayInputStream(bytarr));
                    if (sizesKnown != -1) {
                        bodySizes.append(',').append(bytarr.length);
                        sizesKnown += bytarr.length;
                    }

//      //      ism.addInputStream(new ByteArrayInputStream(baos.toByteArray()));
//     }
//     catch (IOException e1)
//     {
//      // TODO Auto-generated catch block
//      e1.printStackTrace();
//     }

                } else if (rB instanceof XMLableMessageBody) {
                    bodyTypes.append(",xml");

                    byte[] bytarr;
                    try {
                        bytarr = ("<BODY CLASS=\"" + rB.getClass().getName() + "\">" + ((XMLableMessageBody) rB).toXML()
                                + "</BODY>").getBytes("UTF-8");

                        islist.add(new ByteArrayInputStream(bytarr));

                        if (sizesKnown != -1) {
                            bodySizes.append(',').append(bytarr.length);
                            sizesKnown += bytarr.length;
                        }
                    } catch (UnsupportedEncodingException e) {
                    }
                }
            }
        }

        if (hasBin) {
            if (nResp == 1) {
                if (bodies.get(0) instanceof SerializableMessageBody) {
                    tu.setContentType(SERIALIZED_CONTENT_TYPE);
                } else {
                    tu.setContentType(STREAM_CONTENT_TYPE);
                }

                if (sizesKnown > 0) {
                    tu.setContentSize(sizesKnown);
                }
                tu.setContentInputStream(islist.get(0));
            } else {
                tu.setContentType(COMPOSITE_CONTENT_TYPE);
                tu.setProperty(X_MSG_CONTENT, bodyTypes.substring(1));

                if (sizesKnown > 0) {
                    tu.setProperty(X_MSG_SIZES, bodySizes.substring(1));
                    tu.setContentSize(sizesKnown);
                    tu.setContentInputStream(new SequenceInputStream(Collections.enumeration(islist)));
                } else {
                    InputStreamMultiplexor ism = new InputStreamMultiplexor(islist);
                    tu.setContentInputStream(ism);
                }
            }

            logger.debug("ADOB types: " + (adobTypes != null ? adobTypes.toString() : "(null)"));
            logger.debug("ADOB IDs: " + (adobIDs != null ? adobIDs.toString() : "(null)"));
            logger.debug("ADOB dispos: " + (adobDisps != null ? adobDisps.toString() : "(null)"));

            if (adobTypes != null && adobTypes.length() > 1) {
                tu.setProperty(X_MSG_ADOB_ContentType, adobTypes.substring(0, adobTypes.length() - 1));
            }

            if (adobIDs != null && adobIDs.length() > 1) {
                tu.setProperty(X_MSG_ADOB_ContentID, adobIDs.substring(0, adobIDs.length() - 1));
            }

            if (adobDisps != null && adobDisps.length() > 1) {
                tu.setProperty(X_MSG_ADOB_ContentDisposition, adobDisps.substring(0, adobDisps.length() - 1));
            }

//   if( adobTypes != null && adobTypes.length() > 1 )
//    tu.setProperty( X_MSG_ADOB_ContentType, adobTypes.toString() );
//
//   if( adobIDs != null && adobIDs.length() > 1  )
//    tu.setProperty( X_MSG_ADOB_ContentID, adobIDs.toString() );
//
//   if( adobDisps != null && adobDisps.length() > 1  )
//    tu.setProperty( X_MSG_ADOB_ContentDisposition, adobDisps.toString() );
        } else {
            tu.setContentType(XML_CONTENT_TYPE);

            StringBuffer reply = new StringBuffer();

            reply.append("<MESSAGE><HEADER TYPE=\"");
            reply.append(rType.name());

            if (msgID != null) {
                reply.append("\" ID=\"");
                reply.append(msgID);
            }

            if (addr != null) {
                reply.append("\" ADDRESS=\"");
                reply.append(addr.toString());
            }

            reply.append("\" HANDINGS=\"");
            reply.append(String.valueOf(handings));
            reply.append("\"/><BODYLIST>");

            if (nResp == 0) {
                reply.append("</BODYLIST></MESSAGE>");
                try {
                    byte[] bytarr = reply.toString().getBytes("UTF-8");
                    tu.setContentSize(bytarr.length);
                    tu.setContentInputStream(new ByteArrayInputStream(bytarr));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return tu;
            }

            byte[] bytarr = null;
            try {
                bytarr = reply.toString().getBytes("UTF-8");
                islist.add(0, new ByteArrayInputStream(bytarr));
                sizesKnown += bytarr.length;

                bytarr = "</BODYLIST></MESSAGE>".getBytes("UTF-8");
                islist.add(new ByteArrayInputStream(bytarr));
                sizesKnown += bytarr.length;
            } catch (UnsupportedEncodingException e) {
            }

            tu.setContentSize(sizesKnown);
            tu.setContentInputStream(new SequenceInputStream(Collections.enumeration(islist)));

        }

        return tu;
    }

    static public TransferUnit prepareSyncReply(Message msg) throws IOException {
        TransferUnit tu = new TransferUnit();

        tu.setProperty(X_MSG_TYPE, ReqType.SYNCREPLY.name());
        tu.setProperty(X_MSG_HANDINGS, String.valueOf(msg.countHandings()));
        tu.setProperty(X_MSG_ID, msg.getID());

        return prepareTU(tu, msg.getResponses(), ReqType.SYNCREPLY, null, msg.getID(), msg.countHandings());
 
  /*
  boolean hasBin = false;
  int sizesKnown = 0;
  
  StringBuffer bodyTypes = null;
  StringBuffer bodySizes = null;

  StringBuffer adobTypes = null;
  StringBuffer adobIDs   = null;
  StringBuffer adobDisps = null;

  List<MessageBody> respBodies = msg.getResponses();
  int nResp = 0;

  List<InputStream> islist = new LinkedList<InputStream>( );

  if (respBodies != null)
   nResp = respBodies.size();

  if (nResp != 0)
  {
   bodyTypes = new StringBuffer();
   bodySizes = new StringBuffer();
   
   
   int sz;
   
//   Iterator iter = respBodies.iterator();

   for (MessageBody rB : respBodies )
   {
    if (rB instanceof ADOBMessageBody)
    {
     ADOBMessageBody amb = (ADOBMessageBody)rB;
     
     hasBin = true;
     bodyTypes.append(",stream");
     
     islist.add( amb.getInputStream() );
     
     sz = (int) amb.getContentSize();
     
     if( sizesKnown != -1 && sz > 0 )
     {
      bodySizes.append(',').append(sz);
      sizesKnown+=sz;
     }
     else
     {
      sizesKnown=-1;
      bodySizes=null;
     }
     
     if( adobTypes == null )
      adobTypes = new StringBuffer();

     if( adobIDs == null )
      adobIDs = new StringBuffer();

     if( adobDisps == null )
      adobDisps = new StringBuffer();
     
     adobTypes.append(amb.getContentType()).append(',');
     
     if( amb.getContentID() != null )
      adobIDs.append(amb.getContentID());
     
     adobIDs.append(',');
     
     if( amb.getMimeDisposition() != null )
      adobDisps.append(amb.getMimeDisposition());
     
     adobDisps.append(',');
    }
    else if (rB instanceof SerializableMessageBody)
    {
     hasBin = true;
     bodyTypes.append(",serialized");

//     try
//     {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream e = new ObjectOutputStream(baos);
      e.writeObject(rB);
      e.close();

      byte[] bytarr = baos.toByteArray();
      
      islist.add( new ByteArrayInputStream(bytarr) );
      if( sizesKnown != -1 )
      {
       bodySizes.append(',').append(bytarr.length);
       sizesKnown+=bytarr.length;
      }

//      //      ism.addInputStream(new ByteArrayInputStream(baos.toByteArray()));
//     }
//     catch (IOException e1)
//     {
//      // TODO Auto-generated catch block
//      e1.printStackTrace();
//     }

    }
    else if (rB instanceof XMLableMessageBody)
    {
     bodyTypes.append(",xml");

     byte[] bytarr;
     try
     {
      bytarr = ("<BODY CLASS=\""
        + rB.getClass().getName() + "\">" + ((XMLableMessageBody)rB).toXML() + "</BODY>")
        .getBytes("UTF-8");

      islist.add( new ByteArrayInputStream(bytarr) );

      if( sizesKnown != -1 )
      {
       bodySizes.append(',').append(bytarr.length);
       sizesKnown+=bytarr.length;
      }
     }
     catch (UnsupportedEncodingException e)
     {
     }
    }
   }
  }

  if (hasBin)
  {
   if( nResp == 1 )
   {
    if(respBodies.get(0) instanceof SerializableMessageBody)
     tu.setContentType(SERIALIZED_CONTENT_TYPE);
    else
     tu.setContentType(STREAM_CONTENT_TYPE);

    if( sizesKnown > 0 )
     tu.setContentSize(sizesKnown);
    tu.setContentInputStream(islist.get(0));
   }
   else
   {
    tu.setContentType(COMPOSITE_CONTENT_TYPE);
    tu.setProperty(X_MSG_CONTENT, bodyTypes.substring(1));

    if (sizesKnown > 0)
    {
     tu.setProperty(X_MSG_SIZES, bodySizes.substring(1));
     tu.setContentSize(sizesKnown);
     tu.setContentInputStream(new SequenceInputStream(Collections
       .enumeration(islist)));
    }
    else
    {
     InputStreamMultiplexor ism = new InputStreamMultiplexor(islist);
     tu.setContentInputStream(ism);
    }
   }

   if( adobTypes != null )
    tu.setProperty( X_MSG_ADOB_ContentType, adobTypes.substring(0,adobTypes.length()-1) );

   if( adobIDs != null )
    tu.setProperty( X_MSG_ADOB_ContentID, adobIDs.substring(0,adobIDs.length()-1) );

   if( adobDisps != null )
    tu.setProperty( X_MSG_ADOB_ContentDisposition, adobDisps.substring(0,adobDisps.length()-1) );
  }
  else
  {
   tu.setContentType(XML_CONTENT_TYPE);

   StringBuffer reply = new StringBuffer();

   reply.append("<MESSAGE><HEADER TYPE=\"");
   reply.append(ReqType.SYNCREPLY.name());

   if( msg.getID() != null )
   {
    reply.append("\" ID=\"");
    reply.append(msg.getID());
   }

   reply.append("\" HANDINGS=\"");
   reply.append(String.valueOf(msg.countHandings()));
   reply.append("\"/><BODYLIST>");

   if( nResp == 0 )
   {
    reply.append("</BODYLIST></MESSAGE>");
    try
    {
     byte[] bytarr = reply.toString().getBytes("UTF-8");
     tu.setContentSize( bytarr.length );
     tu.setContentInputStream(new ByteArrayInputStream(bytarr));
    }
    catch (UnsupportedEncodingException e)
    {
     // TODO Auto-generated catch block
     e.printStackTrace();
    }

    return tu;
   }
   
   byte[] bytarr = null;
   try
   {
    bytarr = reply.toString().getBytes("UTF-8");
    islist.add(0, new ByteArrayInputStream(bytarr));
    sizesKnown += bytarr.length;

    bytarr = "</BODYLIST></MESSAGE>".getBytes("UTF-8");
    islist.add(new ByteArrayInputStream(bytarr));
    sizesKnown += bytarr.length;
   }
   catch (UnsupportedEncodingException e)
   {
   }

   tu.setContentSize( sizesKnown );
   tu.setContentInputStream(new SequenceInputStream(Collections.enumeration( islist )) );

  }

  return tu;
  */
    }

    static public TransferUnit prepareException(Message msg, MessageDeliveryException e) {
        TransferUnit tu = new TransferUnit();
        tu.setContentType(XML_CONTENT_TYPE);

        try {
            tu.setContentInputStream(new ByteArrayInputStream(
                    ("<MESSAGE><HEADER TYPE=\"" + ReqType.EXCEPTION.name() + "\" HANDINGS=\"0\" ID=\"" + msg.getID()
                            + "\"/><BODYLIST><BODY CLASS=\"" + e.getClass().getName() + "\">" + e.toXML()
                            + "</BODY></BODYLIST></MESSAGE>").getBytes("UTF-8")));
        } catch (UnsupportedEncodingException x) {
            // TODO Auto-generated catch block
            x.printStackTrace();
        }

        return tu;

    }

    static public TransferUnit preparePoll() {
        return prepareSimpleRequest(ReqType.POLL);
    }

    public static TransferUnit prepareCloseRequest() {
        return prepareSimpleRequest(ReqType.CLOSE);
    }

    public static TransferUnit prepareCloseAckRequest() {
        return prepareSimpleRequest(ReqType.CLOSEACK);
    }

    public static TransferUnit prepareNOPRequest() {
        return prepareSimpleRequest(ReqType.NOP);
    }

    private static TransferUnit prepareSimpleRequest(ReqType type) {
        TransferUnit tu = new TransferUnit();

        tu.setContentType(STREAM_CONTENT_TYPE);
        tu.setContentSize(-1);
        tu.setMethod("GET");
        tu.setProperty(X_MSG_TYPE, type.name());

        return tu;
    }


    public static NetworkInputRequest parseResponse(String contentType, InputStream is, Iterator<StringPair> prop,
            ADOBFactory adobFact) throws ProtocolException, IOException {
        String pTypes = null;
        String pSizes = null;
        String adobTypes = null;
        String adobIDs = null;
        String adobDisps = null;
        int contentLength = -1;

        NetworkInputRequest req = new NetworkInputRequest();
        while (prop.hasNext()) {
            StringPair pair = prop.next();
            String key = pair.getFirst();

// com.pri.shex.webapp.Log.info("Header: "+key+"="+pair.getSecond());
//System.out.println("Processing request: "+key+"="+pair.getSecond());

            if (X_MSG_TYPE.equalsIgnoreCase(key)) {
                req.setType(pair.getSecond());
            }
            if (X_MSG_MSGTYPE.equalsIgnoreCase(key)) {
                req.setMessageType(pair.getSecond());
            } else if (X_MSG_ADDRESS.equalsIgnoreCase(key)) {
                req.setAddress(pair.getSecond());
            } else if (X_MSG_ID.equalsIgnoreCase(key)) {
                req.setID(pair.getSecond());
            } else if (X_MSG_HANDINGS.equalsIgnoreCase(key)) {
                req.setHandings(Integer.parseInt(pair.getSecond()));
            } else if (X_MSG_CONTENT.equalsIgnoreCase(key)) {
                pTypes = pair.getSecond();
            } else if (X_MSG_SIZES.equalsIgnoreCase(key)) {
                pSizes = pair.getSecond();
            } else if (X_MSG_POLL_LIFETIME.equalsIgnoreCase(key)) {
                req.setPollLifetime(Integer.parseInt(pair.getSecond()));
            } else if (X_MSG_VER.equalsIgnoreCase(key)) {
                req.setInfoVersion(Integer.parseInt(pair.getSecond()));
            } else if (X_MSG_QLEN.equalsIgnoreCase(key)) {
                req.setQueueLength(Integer.parseInt(pair.getSecond()));
            } else if (X_MSG_ADOB_ContentType.equalsIgnoreCase(key)) {
                adobTypes = pair.getSecond();
            } else if (X_MSG_ADOB_ContentID.equalsIgnoreCase(key)) {
                adobIDs = pair.getSecond();
            } else if (X_MSG_ADOB_ContentDisposition.equalsIgnoreCase(key)) {
                adobDisps = pair.getSecond();
            } else if ("Content-Length".equalsIgnoreCase(key)) {
                contentLength = Integer.parseInt(pair.getSecond());
            }

        }

        if (contentType == null) {
            throw new ProtocolException("Invalid response: no \"Content-type\" header");
        }

        if (contentType.equalsIgnoreCase(XML_CONTENT_TYPE)) {

            try {
                return xmlToMessage(parserFactory.newSAXParser(), new InputSource(is), req);
            } catch (ParserConfigurationException e1) {
                Log.error("XML parser error", e1);
                IOException ioe = new IOException("XML parser error");
                ioe.initCause(e1);
                throw ioe;
            } catch (SAXException e) {
                Throwable cause = e.getCause();

                if (cause != null && cause instanceof ProtocolException) {
                    throw (ProtocolException) cause;
                }

                Log.error("XML parser error", e);
                IOException ioe = new IOException("XML parser error");
                ioe.initCause(e);
                throw ioe;
            } catch (IOException e) {
                Log.error("Message read error", e);
                throw e;
            }
        }

        ReqType rType = req.getType();

        if (rType == ReqType.CLOSE || rType == ReqType.CLOSEACK || rType == ReqType.NOP || rType == ReqType.POLL
                || rType == ReqType.IDENTIFYREQ || rType == ReqType.IDENTIFYRESP || rType == ReqType.SESSEXPIRED) {
            return req;
        }

        if (contentType.equalsIgnoreCase(STREAM_CONTENT_TYPE)) {
            if (adobDisps != null && adobDisps.length() == 0) {
                adobDisps = null;
            }

            if (adobIDs != null && adobIDs.length() == 0) {
                adobIDs = null;
            }

            ADOBMessageBody adb = null;
            if (adobFact != null) {
                adb = new OverADOBMessageBody(
                        adobFact.createADOB(M1codec.decode(adobTypes), contentLength, M1codec.decode(adobIDs),
                                M1codec.decode(adobDisps), is, null, false, true));
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamPump.doPump(is, baos);

                adb = new ByteArrayADOBMessageBody(baos.toByteArray());

                if (adobTypes != null) {
                    adb.setContentType(M1codec.decode(adobTypes));
                }

                if (adobDisps != null) {
                    adb.setDisposition(M1codec.decode(adobDisps));
                }

                if (adobIDs != null) {
                    adb.setContentID(M1codec.decode(adobIDs));
                }
            }

            if (rType == ReqType.SYNCREPLY) {
                req.addResponse(adb);
            } else {
                req.setBody(adb);
            }
        } else if (contentType.equalsIgnoreCase(COMPOSITE_CONTENT_TYPE)) {
            if (pTypes != null) {
                SimpleTokenizer pTypTok = new SimpleTokenizer(pTypes, ',');

                List<String> types = new ArrayList<String>(5);

                while (pTypTok.hasMoreTokens()) {
                    types.add(pTypTok.nextToken());
                }

                pTypTok = null;

                SimpleTokenizer cTypTok = null;
                SimpleTokenizer cDispTok = null;
                SimpleTokenizer cIDTok = null;
                SimpleTokenizer szTok = null;

                if (adobDisps != null) {
                    logger.debug(adobDisps);
                    cDispTok = new SimpleTokenizer(adobDisps, ',');
                }

                if (adobIDs != null) {
                    logger.debug(adobIDs);
                    cIDTok = new SimpleTokenizer(adobIDs, ',');
                }

                if (adobTypes != null) {
                    logger.debug(adobTypes);
                    cTypTok = new SimpleTokenizer(adobTypes, ',');
                }

                if (pSizes != null) {
                    logger.debug(pSizes);
                    szTok = new SimpleTokenizer(pSizes, ',');
                }

                Iterator<InputStream> isd = null;

                if (pSizes != null) {
                    isd = new SequentialInputStreamDemultiplexor(is, pSizes);
                } else {
                    isd = new ChankedInputStreamDemultiplexor(is);
                }

                int count = -1;
                while (isd.hasNext()) {

                    InputStream isdis = isd.next();

                    count++;
                    if (count >= types.size()) {
                        throw new ProtocolException("Invalid " + X_MSG_CONTENT + " property");
                    }

                    String type = types.get(count);

//     if (pTypTok.hasMoreTokens())
//      type = pTypTok.nextToken();
//     else
//      throw new ProtocolException("Invalid "+X_MSG_CONTENT+" property");

                    int cntSz = -1;
                    if (szTok != null) {
                        if (!szTok.hasMoreTokens()) {
                            Log.error("Invalid " + X_MSG_SIZES + " property: " + pSizes);
                            throw new ProtocolException("Invalid " + X_MSG_SIZES + " property");
                        }

                        try {
                            cntSz = Integer.parseInt(szTok.nextToken());
                        } catch (NumberFormatException e) {
                            Log.error("Invalid " + X_MSG_SIZES + " property (number format): " + pSizes);
                            throw new ProtocolException("Invalid " + X_MSG_SIZES + " property");
                        }
                    }

                    if (type.equalsIgnoreCase("stream")) {
                        if (cTypTok == null || !cTypTok.hasMoreTokens()) {
                            Log.error("Invalid " + X_MSG_ADOB_ContentType + " property: " + adobTypes);
                            throw new ProtocolException("Invalid " + X_MSG_ADOB_ContentType + " property");
                        }

                        String cntID = null, cntTyp = null, cntDisp = null;

                        cntTyp = M1codec.decode(cTypTok.nextToken());

                        if (cIDTok != null) {
                            if (!cIDTok.hasMoreTokens()) {
                                Log.error("Invalid " + X_MSG_ADOB_ContentID + " property: " + adobIDs);
                                throw new ProtocolException("Invalid " + X_MSG_ADOB_ContentID + " property");
                            }

                            cntID = M1codec.decode(cIDTok.nextToken());
                        }

                        if (cDispTok != null) {
                            if (!cDispTok.hasMoreTokens()) {
                                Log.error("Invalid " + X_MSG_ADOB_ContentDisposition + " property: " + adobDisps);
                                throw new ProtocolException("Invalid " + X_MSG_ADOB_ContentDisposition + " property");
                            }

                            cntDisp = M1codec.decode(cDispTok.nextToken());
                        }

                        ADOBMessageBody adb = null;
                        if (adobFact != null) {
                            adb = new OverADOBMessageBody(
                                    adobFact.createADOB(cntTyp, cntSz, cntID, cntDisp, isdis, null, false,
                                            count + 1 == types.size()));
                        } else {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            StreamPump.doPump(isdis, baos);

                            adb = new ByteArrayADOBMessageBody(baos.toByteArray());
                            adb.setContentType(cntTyp);
                            adb.setDisposition(cntDisp);
                            adb.setContentID(cntID);

                            if (cntSz != -1 && cntSz != adb.getContentSize()) {
                                Log.warn("Declared content size ({0}) and real size ({1}) are not equal", cntSz,
                                        adb.getContentSize());
                            }
                        }

                        req.addResponse(adb);
                    } else if (type.equalsIgnoreCase("xml")) {
                        try {
                            NetworkInputRequest nir = xmlToMessage(parserFactory.newSAXParser(), new InputSource(isdis),
                                    null);
                            req.addResponse(nir.getBody());
                        } catch (ParserConfigurationException e1) {
                            Log.error("XML parser error", e1);
                            IOException ioe = new IOException("XML parser error");
                            ioe.initCause(e1);
                            throw ioe;
                        } catch (SAXException e) {
                            Throwable cause = e.getCause();

                            if (cause != null && cause instanceof ProtocolException) {
                                throw (ProtocolException) cause;
                            }

                            Log.error("XML parser error", e);
                            IOException ioe = new IOException("XML parser error");
                            ioe.initCause(e);
                            throw ioe;
                        } catch (IOException e) {
                            Log.error("Message read error", e);
                            throw e;
                        }

                    } else if (type.equalsIgnoreCase("serialized")) {
                        try {
                            req.addResponse((MessageBody) new ObjectInputStream(isdis).readObject());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }

            }
        } else if (contentType.equalsIgnoreCase(SERIALIZED_CONTENT_TYPE)) {
            try {

                ObjectInputStream ois = new ObjectInputStream(is);
                while (true) {
                    Object obj = ois.readObject();

                    if (rType == ReqType.SYNCREPLY) {
                        req.addResponse((MessageBody) obj);
                    } else {
                        req.setBody((MessageBody) obj);
                    }
                }
            } catch (EOFException e) {
            } catch (Exception ex) {
                IOException ioex = new IOException("Serialized message parse error");
                ioex.initCause(ex);
                throw ioex;
            }
        }

        return req;
    }

    public static NetworkInputRequest xmlToMessage(SAXParser parser, InputSource iS, NetworkInputRequest nreq)
            throws SAXException, IOException {
        MessageParseHandler ph = new MessageParseHandler(nreq);
        parser.parse(iS, ph);

        return ph.getInputRequest();
    }

    public static Options getOptions() {
        return options;
    }

    public static void setOptions(Options opt) {
        options = opt;
    }


}

class MessageParseHandler extends DefaultHandler {

    final static int NOTHING = 0;
    final static int BEAN = 1;
    final static int SERIAL = 2;
    final static int XML = 3;

    NetworkInputRequest msg;
    boolean delegateCalls;
    boolean bodyListSection;
    int nestedBodies;
    int collecting = NOTHING;
    StringBuffer bodyCollector;
    Class<?> bodyClass;

    LightSAXParser currentParser;

    public MessageParseHandler(NetworkInputRequest nreq) {
        msg = nreq;
    }

    public NetworkInputRequest getInputRequest() {
        return msg;
    }

    public void startDocument() {
        if (msg == null) {
            msg = new NetworkInputRequest();
        }

        delegateCalls = false;
        bodyListSection = false;
        nestedBodies = 0;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (msg == null) {
            return;
        }

        // System.out.println("qName="+qName);

        if (delegateCalls) {
            if (qName.equals("BODY")) {
                nestedBodies++;
            }

            currentParser.startElement(uri, localName, qName, attributes);
        } else if (qName.equals("HEADER")) {
            int nAttr = attributes.getLength();

            for (int i = 0; i < nAttr; i++) {
                // System.out.println("Attr:
                // "+attributes.getQName(i)+"="+attributes.getValue(i));

                if (attributes.getQName(i).equalsIgnoreCase("TYPE")) {
                    msg.setType(attributes.getValue(i));
                } else if (attributes.getQName(i).equalsIgnoreCase("ADDRESS")) {
                    msg.setAddress(attributes.getValue(i));
                } else if (attributes.getQName(i).equalsIgnoreCase("MSGTYPE")) {
                    msg.setMessageType(attributes.getValue(i));
                } else if (attributes.getQName(i).equalsIgnoreCase("ID")) {
                    msg.setID(attributes.getValue(i));
                } else if (attributes.getQName(i).equalsIgnoreCase("HANDINGS")) {
                    msg.setHandings(Integer.parseInt(attributes.getValue(i)));
                }

            }

        } else if (qName.equals("BODYLIST")) {
            bodyListSection = true;
        } else if (qName.equals("BODY")) {
            String className = attributes.getValue("CLASS");

            if (className == null) {
                msg = null;
                return;
            }

            try {
                bodyClass = Class.forName(className);

                if (NetworkMessengerProtocol.xmlSerializableMBClass.isAssignableFrom(bodyClass)) {
                    collecting = SERIAL;
                    if (bodyCollector == null) {
                        bodyCollector = new StringBuffer();
                    }
                } else if (NetworkMessengerProtocol.autoParserMBClass.isAssignableFrom(bodyClass)) {
                    AutoParserMessageBody body = (AutoParserMessageBody) bodyClass.newInstance();
                    currentParser = body.getParser();

                    if (bodyListSection) {
                        msg.addResponse(body);
                    } else {
                        msg.setBody(body);
                    }

                    delegateCalls = true;
                } else if (NetworkMessengerProtocol.beanMBClass.isAssignableFrom(bodyClass)) {
                    collecting = BEAN;
                    if (bodyCollector == null) {
                        bodyCollector = new StringBuffer();
                    }
                } else if (NetworkMessengerProtocol.autoRestoreMBClass.isAssignableFrom(bodyClass)) {
                    collecting = XML;
                    if (bodyCollector == null) {
                        bodyCollector = new StringBuffer();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
                msg = null;
            }

            return;
        }

    }

    public void characters(char[] ch, int start, int length) {
        // System.out.println("Got chars [["+new String(ch,start,length)+"]]" );

        if (msg == null) {
            return;
        }

        if (collecting != NOTHING) {
            bodyCollector.append(ch, start, length);
        } else if (delegateCalls) {
            currentParser.characters(ch, start, length);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {

        if (msg == null) {
            return;
        }

        if (qName.equals("BODY") && nestedBodies == 0) {
            if (collecting != NOTHING) {
                MessageBody body = null;
                if (collecting == SERIAL) {
                    // System.out.println(bodyCollector.toString());

                    body = XMLSerializableMessageBody.restore(bodyCollector.toString());
                } else if (collecting == XML) {
                    try {
                        body = (MessageBody) bodyClass.newInstance();
                        ((XMLable) body).restore(bodyCollector.toString());
                    } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    // System.out.println(bodyCollector.toString());

                    try {
                        XMLDecoder d = new XMLDecoder(
                                new ByteArrayInputStream(bodyCollector.toString().getBytes("UTF-8")));
                        body = (MessageBody) d.readObject();
                        d.close();
                    } catch (UnsupportedEncodingException e) {
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }
                collecting = NOTHING;
                bodyCollector.setLength(0);

                if (bodyListSection) {
                    msg.addResponse(body);
                } else {
                    msg.setBody(body);
                }
            }

            delegateCalls = false;
            bodyClass = null;

            return;
        }

        if (qName.equals("BODYLIST") && nestedBodies == 0) {
            bodyListSection = false;
            return;
        }

        if (delegateCalls) {
            if (qName.equals("BODY")) {
                nestedBodies--;
            }

            currentParser.endElement(namespaceURI, localName, qName);
        }

    }

}
