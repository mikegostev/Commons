/*
 * Created on 26.09.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import com.pri.util.LightSAXParser;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public interface AutoParserMessageBody extends XMLableMessageBody {

    public LightSAXParser getParser();
}
