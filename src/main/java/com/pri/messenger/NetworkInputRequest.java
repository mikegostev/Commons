/*
 * Created on 25.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.messenger.NetworkMessengerProtocol.ReqType;
import java.util.ArrayList;
import java.util.List;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class NetworkInputRequest {

 /*
 static public final int REQ_INVALID       =0;
 static public final int REQ_POLL          =1;
 static public final int REQ_SYNCREQUEST   =2;
 static public final int REQ_SYNCREPLY     =3;
 static public final int REQ_ASYNCREQUEST  =4;
 static public final int REQ_ASYNCREPLY    =5;
 static public final int REQ_EXCEPTION     =6;
 static public final int REQ_IDENTIFY      =7;
 static public final int REQ_SESSEXPIRED   =8;
 public static final int REQ_CLOSE         =9;

*/

    ReqType type = ReqType.INVALID;
    String ID;
    String address;
    MessageBody body;
    List<MessageBody> responses;
    int handings;
    String messageType;
    int pollLifetime;
    int queueLength;
    int infoVersion;

    public int getInfoVersion() {
        return infoVersion;
    }

    public void setInfoVersion(int infoVersion) {
        this.infoVersion = infoVersion;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MessageBody getBody() {
        return body;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }

    public List<MessageBody> getResponses() {
        return responses;
    }

    public void addResponse(MessageBody body) {
        if (responses == null) {
            responses = new ArrayList<MessageBody>(3);
        }

        responses.add(body);
    }

    public void setResponses(List<MessageBody> responses) {
        this.responses = responses;
    }


    public ReqType getType() {
        return type;
    }


    public void setType(String typeStr) {
        try {
            type = ReqType.valueOf(typeStr);
        } catch (Exception e) {
            type = ReqType.INVALID;
        }
    }


    public void setType(ReqType type) {
        this.type = type;
    }

    public int getHandings() {
        return handings;
    }

    public void setHandings(int handings) {
        this.handings = handings;
    }

    /**
     * @return Returns the messageType.
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType The messageType to set.
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getPollLifetime() {
        return pollLifetime;
    }

    public void setPollLifetime(int lt) {
        pollLifetime = lt;
    }
}
