package com.pri.messenger;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

final public class Address implements Serializable, Comparable {

    static final long serialVersionUID = 1796141543030890737L;

    final static public String LOCAL_HOST = "0.0.0.0";
    final static public String NET_PEER_HOST = "255.255.255.255";
    final static public String MESSAGE_SCHEME = "message";
    static final URI localBaseURI;

    static {
        URI a = null;

        try {
            a = new URI("message://0.0.0.0/");
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        localBaseURI = a;

    }

    private URI uri;
    private String stringRep;

    public Address(URI uri) {
        this.uri = uri;
        stringRep = uri.toString();
    }

    public Address(String spec) {
        uri = localBaseURI.resolve(spec);
        stringRep = uri.toString();
    }

    public Address(Address base, String spec) {
        uri = base.uri.resolve(spec);
        stringRep = uri.toString();
    }

    public Address(String host, String sess, String srv) {
        StringBuffer path = new StringBuffer();

        if (sess != null) {
            path.append("/");
            path.append(sess);
        }

        path.append("/");

        if (srv != null) {
            path.append(srv);
        }

        try {
            uri = new URI(MESSAGE_SCHEME, host, path.toString(), null);
            stringRep = uri.toString();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Address newServerAddress(String srv) {
        return new Address(NET_PEER_HOST, null, srv);
    }

    public static Address newClientAddress(String sessionString, String srv) {
        return new Address(NET_PEER_HOST, sessionString, srv);
    }

    public void setLocal() {
        try {
            uri = new URI(uri.getScheme(), LOCAL_HOST, uri.getPath(), null);
            stringRep = uri.toString();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String toString() {
        return stringRep;
    }

    public boolean isLocal() {
        return uri.getHost().equals(LOCAL_HOST);
    }

    public boolean isExternal() {
        return !uri.getHost().equals(LOCAL_HOST);
    }

    public boolean equals(Address another) {
        return this.toString().equals(another.toString());
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getLocal() {
        String path = uri.getPath();

        try {
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (IndexOutOfBoundsException e) {
            return path;
        }
    }

    public String getSession() {
        String path = uri.getPath();

        try {
            return path.substring(path.indexOf('/') + 1, path.lastIndexOf('/'));
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        return toString().compareTo(arg0.toString());
    }

}
