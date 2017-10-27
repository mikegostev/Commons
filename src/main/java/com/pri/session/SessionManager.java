package com.pri.session;

public interface SessionManager {

    public ClientSession getSession(String sessionKey);

    public ClientSession getSession(String sessionKey, RequestData req);
}
