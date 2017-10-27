package com.pri.util;

/*
 * Created on 22.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class XMLHelper {

    static public String quotByEntity(String str) {
        if (str.indexOf('&') != -1) {
            int len = str.length();
            StringBuffer sb = new StringBuffer(len + 4 * 10);
            int offset = 0;
            int pos;

//   while( (pos=str.indexOf('<',offset)) != -1 && offset < len )
            do {
                if ((pos = str.indexOf('&', offset)) == -1) {
                    sb.append(str.substring(offset));
                    break;
                }

                sb.append(str.substring(offset, pos));
                sb.append("&amp;");
                offset = pos + 1;
            } while (offset < len);

            str = sb.toString();
        }

        if (str.indexOf('<') != -1) {
            int len = str.length();
            StringBuffer sb = new StringBuffer(len + 3 * 10);
            int offset = 0;
            int pos;

//   while( (pos=str.indexOf('<',offset)) != -1 && offset < len )
            do {
                if ((pos = str.indexOf('<', offset)) == -1) {
                    sb.append(str.substring(offset));
                    break;
                }

                sb.append(str.substring(offset, pos));
                sb.append("&lt;");
                offset = pos + 1;
            } while (offset < len);

            str = sb.toString();
        }

        return str;
    }
}
