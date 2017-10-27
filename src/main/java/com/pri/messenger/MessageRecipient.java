package com.pri.messenger;

public interface MessageRecipient {

    //public MessageBody respond (Message message) ;
    public Message receive(Message message);
    //public Address getAddress () ;
}
