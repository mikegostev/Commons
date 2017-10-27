/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.session;

import com.pri.util.Interval;
import java.util.List;

/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public interface SessionPool<CS extends ClientSession>

{

    public String createSession(CS cl) throws UnableGenerateSessionKeyException;

    public CS getSessionByUser(String uname);

    public CS verifySession(String k, RequestData rq);

    public CS getClientData(String k);

    public void removeExpiredSessions();

    public List<CS> listSessions();

    public List<CS> listSessions(List<Object> uIDs, String sessK, String addr, Interval startTimeIval,
            Interval updateTimeIval, Interval expireTimeIval);

    public boolean dropSession(String k);

    public void destroy();

    public void dropUserSession(String user);

    public void dropUserSession(int userID);

    public CS getSession(int userID);

}
