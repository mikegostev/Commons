package com.pri.messenger;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private Address address;
    private MessageBody body;
    private List<MessageBody> auxBodies;
    private List<MessageBody> responses;
    private boolean sync;
    private String ID;
    private int handingsCounter = 0;
    private Object senderData;
    private String type;

    public Message(Address address) {
        this.address = address;
        body = null;
    }

    public Message(Address address, String sBody) {
        this.address = address;
        body = new StringBody(sBody);
    }

    public Message(String address, String sBody) {
        this.address = new Address(address);
        body = new StringBody(sBody);
    }

    public Message(Address address, MessageBody body) {
        this.address = address;
        this.body = body;
    }

    public void addResponse(MessageBody response) {

        if (!sync) {
            return;
        }

        if (responses == null) {
            responses = new ArrayList<MessageBody>();
        }

        responses.add(response);
    }

    public List<MessageBody> getResponses() {
        return responses;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public MessageBody getBody() {
        if (auxBodies != null && auxBodies.size() > 0) {
            return auxBodies.get(0);
        }

        return body;
    }

    public List<MessageBody> getBodies() {
        if (body != null) {
            List<MessageBody> bl = new ArrayList<MessageBody>(1);
            bl.add(body);
            return bl;
        }

        return auxBodies;
    }

    public void setBody(MessageBody body) {
        this.body = body;
        auxBodies = null;
    }

    public void setBodies(List<MessageBody> bds) {
        body = null;
        auxBodies = bds;
    }

    public void addBody(MessageBody nbdy) {
        if (auxBodies == null) {
            auxBodies = new ArrayList<MessageBody>(5);
        }

        if (body != null) {
            auxBodies.add(body);
        }

        auxBodies.add(nbdy);

        body = null;
    }

    public int countBodies() {
        if (auxBodies != null) {
            return auxBodies.size();
        }

        if (body != null) {
            return 1;
        }

        return 0;
    }

    /**
     * @return Returns the sync.
     */
    public boolean isSync() {
        return sync;
    }

    /**
     * @param sync The sync to set.
     */
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }

    public void setResponses(List<MessageBody> responses) {
        this.responses = responses;
    }

    /**
     * @return Returns the countRecipients.
     */
    public void delivered() {
        handingsCounter++;
    }

    public int countHandings() {
        return handingsCounter;
    }

    public void resetHandingsCounter() {
        handingsCounter = 0;
    }

    public void setHandingsCounter(int n) {
        handingsCounter = n;
    }

    /**
     * @return Returns the senderData.
     */
    public Object getSenderData() {
        return senderData;
    }

    /**
     * @param senderData The senderData to set.
     */
    public void setSenderData(Object clientData) {
        this.senderData = clientData;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }


}
