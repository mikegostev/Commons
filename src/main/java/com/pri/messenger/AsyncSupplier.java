package com.pri.messenger;


public interface AsyncSupplier {

    public void addRecipient(MessageRecipient consumer, Address address);

    public void removeRecipient(MessageRecipient consumer, Address address);
}
