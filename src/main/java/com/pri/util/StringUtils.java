/*
 * Created on 24.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;

import com.pri.util.stream.StreamPump;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class StringUtils {


    public static class ReplacePair implements Comparable<ReplacePair> {

        char subject;
        String replacement;

        public ReplacePair() {
        }

        public ReplacePair(char first, String second) {
            this.subject = first;
            this.replacement = second;
        }

        public char getSubject() {
            return subject;
        }

        public void setSubject(char first) {
            this.subject = first;
        }

        public String getReplacement() {
            return replacement;
        }

        public void setReplacement(String second) {
            this.replacement = second;
        }

        @Override
        public int compareTo(ReplacePair toCmp) {
            return subject - toCmp.getSubject();
        }

        @Override
        public boolean equals(Object o) {
            return subject == ((ReplacePair) o).getSubject();
        }

        @Override
        public int hashCode() {
            return subject;
        }

    }

// static final char[] htmlEscChars = new char[]{  '"',     '\'',   '<',   '>',   '&'};
// static final String[] htmlEntity = new String[]{"&quot;","&#39;","&lt;","&gt;","&amp;"};
// static final char[] escChars = {'\'','"','\\','\0'};

// static final char[][] escPairsO = {{'\'','\''},{'"','"'},{'\\','\\'},{'\0','0'},{'\n','n'},{'\r','r'}};

    static final ReplacePair[] htmlPairs = {new ReplacePair('"', "&quot;"), new ReplacePair('\'', "&#39;"),
            new ReplacePair('<', "&lt;"), new ReplacePair('>', "&gt;"), new ReplacePair('&', "&amp;"),};

    static final ReplacePair[] cStrPairs = {new ReplacePair('\'', "\\'"), new ReplacePair('"', "\\\""),
            new ReplacePair('\\', "\\\\"), new ReplacePair('\0', "\\0"), new ReplacePair('\n', "\\n"),
            new ReplacePair('\r', "\\r")};

    static final ReplacePair[] jsonPairs = {new ReplacePair('"', "\\\""), new ReplacePair('\\', "\\\\"),
            new ReplacePair('\0', "\\0"), new ReplacePair('\n', "\\n"), new ReplacePair('\r', "\\r")};

// static final CharPair[] escPairs = {
//  new CharPair('\'','\''),
//  new CharPair('"','"'),
//  new CharPair('\\','\\'),
//  new CharPair('\0','0'),
//  new CharPair('\n','n'),
//  new CharPair('\r','r')
//  };

// static final Comparator<char[]> pairComp = new Comparator<char[]>()
// {
//  public int compare(char[] arg0, char[] arg1)
//  {
//   return arg0[0]-arg1[0];
//  }
// };

    static {
        Arrays.sort(htmlPairs);
        Arrays.sort(cStrPairs);
        Arrays.sort(jsonPairs);
    }

    /**
     *
     */
    private StringUtils() {
    }

// Gives the following order (number of digits takes precedence of tail )
// A000
// A000B01
// A00B01
// A0000001A02
// A001B01
// A01A02
// A01B01
// A01B02
// A02B01
// A2B01
// A11B01
// AB02B01

    public static int naturalCompare(String str1, String str2) {
        int l1 = str1.length();
        int l2 = str2.length();

        int ml = l1 < l2 ? l1 : l2;

        int v1, v2;

        int i = 0;

        do {
            for (; i < ml && str1.charAt(i) == str2.charAt(i) && (!Character.isDigit(str1.charAt(i))) && (!Character
                    .isDigit(str2.charAt(i))); i++) {
                ;
            }

            if (i == ml) {
                break;
            }

            if (Character.isDigit(str1.charAt(i))) {
                v1 = str1.charAt(i) - '0';
            } else {
                return str1.charAt(i) - str2.charAt(i);
            }

            if (Character.isDigit(str2.charAt(i))) {
                v2 = str2.charAt(i) - '0';
            } else {
                return str1.charAt(i) - str2.charAt(i);
            }

            i++;

            int j = i;
            for (; j < l1 && Character.isDigit(str1.charAt(j)); j++) {
                v1 = v1 * 10 + (str1.charAt(j) - '0');
            }

            int k = i;
            for (; k < l2 && Character.isDigit(str2.charAt(k)); k++) {
                v2 = v2 * 10 + (str2.charAt(k) - '0');
            }

            if (v1 != v2) {
                return v1 - v2;
            }

            if (j != k) {
                return k - j;
            }

            i = j;
        } while (i < ml);

        return l1 - l2;
    }

//Gives the following order (tail takes precedence of number of digits )
// A000
// A00B01
// A000B01
// A01A02
// A0000001A02
// A01B01
// A001B01
// A01B02
// A2B01
// A02B01
// A11B01
// AB02B01

    public static int naturalCompare2(String str1, String str2) // Wrong algorithms gives Equals: A01B01 and A001B01
    {
        int l1 = str1.length();
        int l2 = str2.length();

        int ml = l1 < l2 ? l1 : l2;

        int v1 = 0, v2 = 0;

        int i = 0;

        for (; i < ml && str1.charAt(i) == str2.charAt(i) && (!Character.isDigit(str1.charAt(i))) && (!Character
                .isDigit(str2.charAt(i))); i++) {
            ;
        }

        int j = i;
        for (; j < l1 && Character.isDigit(str1.charAt(j)); j++) {
            v1 = v1 * 10 + (str1.charAt(j) - '0');
        }

        int k = i;
        for (; k < l2 && Character.isDigit(str2.charAt(k)); k++) {
            v2 = v2 * 10 + (str2.charAt(k) - '0');
        }

        if (i == j || i == k) {
            if (i < l1) {
                if (i < l2) {
                    return str1.charAt(i) - str2.charAt(i);
                } else {
                    return 1;
                }
            } else if (i == l2) {
                return l1 - l2;
            } else {
                return -1;
            }
        } else if (v1 == v2) {
            if (j < l1) {
                if (k < l2) {
                    if (j == k) {
                        return naturalCompare2(str1.substring(j), str2.substring(k));
                    }

                    int res = naturalCompare2(str1.substring(j), str2.substring(k));

                    if (res == 0) {
                        return j - k;
                    }

                    return res;
                } else {
                    return 1;
                }
            } else if (k < l2) {
                return -1;
            } else {
                return l1 - l2;
            }
        } else {
            return v1 - v2;
        }
    }

    public static String replace(String str, ReplacePair[] pairs) {
        StringBuffer sb = null;
        int cPos, ePos, mPos;

        ReplacePair actPair = null;

        cPos = 0;
        while (cPos < str.length()) {

            ePos = Integer.MAX_VALUE;
            for (int i = 0; i < pairs.length; i++) {
                mPos = str.indexOf(pairs[i].getSubject(), cPos);

                if (mPos == -1) {
                    continue;
                }

                if (mPos < ePos) {
                    ePos = mPos;
                    actPair = pairs[i];
                }
            }

            if (ePos == Integer.MAX_VALUE) {
                if (sb == null) {
                    return str;
                }

                sb.append(str.substring(cPos));
                return sb.toString();
            }

            if (sb == null) {
                sb = new StringBuffer(str.length() * 2);
            }

            sb.append(str.substring(cPos, ePos));
            sb.append(actPair.getReplacement());

            cPos = ePos + 1;
        }

        if (sb != null) {
            return sb.toString();
        }

        return str;
    }

    public static StringBuffer appendReplaced(StringBuffer sb, String str, ReplacePair[] pairs) {
        try {
            appendReplaced((Appendable) sb, str, pairs);
        } catch (IOException e) {
        }

        return sb;
    }

    public static StringBuilder appendReplaced(StringBuilder sb, String str, ReplacePair[] pairs) {
        try {
            appendReplaced((Appendable) sb, str, pairs);
        } catch (IOException e) {
        }

        return sb;
    }


    public static Appendable appendReplaced(Appendable sb, String str, ReplacePair[] pairs) throws IOException {
        int len = 0;
        if (str == null || (len = str.length()) == 0) {
            return sb;
        }

        ReplacePair pair = new ReplacePair();

        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            pair.setSubject(ch);

            int ind = Arrays.binarySearch(pairs, pair);
            if (ind >= 0) {
                sb.append(pairs[ind].getReplacement());
            } else {
                sb.append(ch);
            }
        }

        return sb;
    }


    /**
     * Adds backslashes before every char ch
     */
    public static StringBuilder appendBackslashed(StringBuilder sb, String str, char ch) {
        try {
            appendEscaped((Appendable) sb, str, ch, '\\');
        } catch (IOException e) {
        }

        return sb;
    }

    public static String escapeCStr(String str) {
        return replace(str, cStrPairs);
    }


    public static StringBuffer appendEscaped(StringBuffer sb, String str, char ch, char escChar) {
        try {
            appendEscaped((Appendable) sb, str, ch, escChar);
        } catch (IOException e) {
        }

        return sb;
    }

    public static StringBuilder appendEscaped(StringBuilder sb, String str, char ch, char escCh) {
        try {
            appendEscaped((Appendable) sb, str, ch, escCh);
        } catch (IOException e) {
        }

        return sb;
    }


    public static Appendable appendEscaped(Appendable sb, String str, char[] chs, char escChar) throws IOException {
        int len = str.length();
        int esclen = chs.length;

        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);

            for (int j = 0; j < esclen; j++) {
                if (c == chs[j]) {
                    sb.append(escChar);
                    break;
                }
            }

            sb.append(c);

        }

        return sb;
    }

    public static Appendable appendEscaped(Appendable sb, String str, char ch, char escChar) throws IOException {
        int cPos, ePos;

        cPos = 0;
        while (cPos < str.length()) {
            ePos = str.indexOf(ch, cPos);

            if (ePos == -1) {
                if (cPos == 0) {
                    sb.append(str);
                } else {
                    sb.append(str.substring(cPos));
                }

                return sb;
            }

            sb.append(str.substring(cPos, ePos));
            sb.append(escChar);
            sb.append(ch);

            cPos = ePos + 1;
        }

        return sb;
    }

    /**
     * Adds backslashes before every char ch
     */


    public static StringBuilder appendAsCStr(StringBuilder sb, String str) {
        return appendReplaced(sb, str, cStrPairs);
    }

    public static Appendable appendAsCStr(Appendable sb, String str) throws IOException {
        return appendReplaced(sb, str, cStrPairs);
    }

    public static Appendable appendAsJSONStr(Appendable sb, String str) throws IOException {
        return appendReplaced(sb, str, jsonPairs);
    }


    public static String escapeBy(String str, char ch, char escCh) {
        StringBuffer sb = null;
        int cPos, ePos;

        cPos = 0;
        while (cPos < str.length()) {
            ePos = str.indexOf(ch, cPos);

            if (ePos == -1) {
                if (sb == null) {
                    return str;
                }

                sb.append(str.substring(cPos));
                return sb.toString();

            }

            if (sb == null) {
                sb = new StringBuffer(str.length() * 2);
            }

            sb.append(str.substring(cPos, ePos));
            sb.append(escCh);
            sb.append(ch);

            cPos = ePos + 1;
        }

        if (sb != null) {
            return sb.toString();
        }

        return str;
    }

    public static String escapeByBackslash(String str, char ch) {
        return escapeBy(str, ch, '\\');
    }

    public static String escapeBy(String str, String escChars, char escCh) {
        StringBuffer sb = null;
        int cPos, ePos, mPos;

        cPos = 0;
        while (cPos < str.length()) {
            ePos = Integer.MAX_VALUE;
            for (int i = 0; i < escChars.length(); i++) {
                mPos = str.indexOf(escChars.charAt(i), cPos);

                if (mPos == -1) {
                    continue;
                }

                if (mPos < ePos) {
                    ePos = mPos;
                }

            }

            if (ePos == Integer.MAX_VALUE) {
                if (sb == null) {
                    return str;
                }

                sb.append(str.substring(cPos));
                return sb.toString();
            }

            if (sb == null) {
                sb = new StringBuffer(str.length() * 2);
            }

            sb.append(str.substring(cPos, ePos));
            sb.append(escCh);
            sb.append(str.charAt(ePos));

            cPos = ePos + 1;
        }

        if (sb != null) {
            return sb.toString();
        }

        return str;
    }

    public static String escapeByBackslash(String str, String escChars) {
        return escapeBy(str, escChars, '\\');
    }


/*
 public static StringBuilder appendSlashed2( StringBuilder sb, String str )
 {
  int cPos,ePos,mPos;
  
  cPos=0;
  int len = str.length();
  while( cPos < len )
  {
   ePos = Integer.MAX_VALUE;
   for( int i=0; i < escChars.length; i++ )
   {
    mPos=str.indexOf(escChars[i],cPos);
    
    if( mPos == -1 )
     continue;
    
    if( mPos < ePos )
     ePos=mPos;
    
   }
   
   if( ePos == Integer.MAX_VALUE )
   {
    if( cPos == 0 )
     sb.append(str);
    else
     sb.append(str.substring(cPos));
    
    return sb;
   }
   
   sb.append(str.substring(cPos,ePos));
   sb.append('\\');
   sb.append(str.charAt(ePos));
   
   cPos=ePos+1;
  }
  
  return sb;
 }
*/

/* 
 public static StringBuilder escape( StringBuilder sb, String str, CharPair[] pairs )
 {
  int len=0;
  if( str == null || ( len=str.length()) == 0 )
   return sb;
  
  CharPair pair = new CharPair();
  
  for( int i=0; i < len; i++ )
  {
   char ch = str.charAt(i);
   pair.setFirst(ch);
   
   int ind = Arrays.binarySearch( pairs, pair );
   if( ind >= 0 )
   {
    sb.append('\\');
    sb.append(pairs[ind].getSecond());
   }
   else
    sb.append(ch);
  }
  
  return sb;
 }
 
 public static String escape( String str )
 {
  int len=0;
  if( str == null || ( len=str.length()) == 0 )
   return str;
  
  CharPair pair = new CharPair();
  
  StringBuilder sb = null;
  
  for( int i=0; i < len; i++ )
  {
   char ch = str.charAt(i);
   pair.setFirst(ch);
   
   int ind = Arrays.binarySearch( escPairs, pair );
   if( ind >= 0 )
   {
    if( sb==null )
    {
     sb=new StringBuilder( str.length()+10 );
     
     if( i != 0 )
      sb.append( str.substring(0,i) );
    }
    
    sb.append('\\');
    sb.append(escPairs[ind].getSecond());
   }
   else if( sb!= null )
    sb.append(ch);
    
  }
  
  return sb==null?str:sb.toString();
 }
*/

    public static String jsString(String str) {
        if (str == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder(str.length() + 10);

        sb.append('\'');
        appendAsCStr(sb, str);
        sb.append('\'');

        return sb.toString();
    }


    public static String xmlEscaped(String s) {
        StringBuilder sb = null;

        int len = s.length();

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);

            if (ch < 0x20 && ch != 0x0D && ch != 0x0A && ch != 0x09) {
                if (sb == null) {
                    sb = new StringBuilder(len + 50);
                    sb.append(s.substring(0, i));
                }

                int rem = ch % 16;

                sb.append("&#").append((ch > 15) ? '1' : '0').append((char) (rem > 9 ? (rem - 10 + 'A') : (rem + '0')))
                        .append(';');
            } else {
                boolean replaced = false;

                for (ReplacePair p : htmlPairs) {
                    if (ch == p.getSubject()) {
                        if (sb == null) {
                            sb = new StringBuilder(len + 50);
                            sb.append(s.substring(0, i));
                        }

                        sb.append(p.getReplacement());
                        replaced = true;
                        break;
                    }
                }

                if (!replaced) {
                    if (sb != null) {
                        sb.append(ch);
                    }
                }
            }
        }

        if (sb != null) {
            return sb.toString();
        }

        return s;
    }


    public static String htmlEscaped(String s) {
        if (s == null) {
            return "";
        }

        return replace(s, htmlPairs);
    }

    public static boolean splitExcelString(String line, String sep, List<String> accum) {
        int spos;
        StringBuilder sb = null;

        while (line.length() > 0) {

            if (line.charAt(0) != '"') // This cell is not quoted
            {
                spos = line.indexOf(sep);

                if (spos < 0) {
                    accum.add(line);
                    return true;
                }

                accum.add(line.substring(0, spos));
                line = line.substring(spos + sep.length());
            } else {
                int qpos;
                int beg = 1;

                while (true) // A quoted cell can contain double quote inside. Let'd build the cell content from parts
                {
                    qpos = line.indexOf('"', beg);

                    if (qpos
                            == -1) // actually this is the erroneous situation - quoted part is not finished by the
                        // quotation symbol.
                    {
                        if (sb != null && sb.length() > 0) {
                            sb.append(line.substring(beg));
                            accum.add(sb.toString());
                        } else {
                            accum.add(line.substring(beg));
                        }

                        return false;
                    } else if (qpos == line.length() - 1) {
                        if (sb != null && sb.length() > 0) {
                            sb.append(line.substring(beg, line.length() - 1));
                            accum.add(sb.toString());
                        } else {
                            accum.add(line.substring(beg, line.length() - 1));
                        }

                        return true;
                    }

                    if (line.charAt(qpos + 1) == '"') // We have found a double quote
                    {
                        if (sb == null) {
                            sb = new StringBuilder(200);
                        }

                        sb.append(line.substring(beg, qpos + 1)); // adding part of the cell to the buffer and continue
                        beg = qpos + 2;
                    } else {
                        if (line.startsWith(sep, qpos + 1)) {
                            if (sb != null && sb.length() > 0) {
                                sb.append(line.substring(beg, qpos));
                                accum.add(sb.toString());
                                sb.setLength(0);
                            } else {
                                accum.add(line.substring(beg, qpos));
                            }

                            line = line.substring(qpos + sep.length() + 1);
                            break;
                        } else // actually this is the erroneous situation - quotation symbol have to be followed by
                        // separator or to be doubled .
                        {
                            if (sb == null) {
                                sb = new StringBuilder(200);
                            }

                            sb.append(line.substring(beg, qpos + 1));
                            beg = qpos + 1;

                            return false;
                        }
                    }

                }
            }
        }

        return true;
    }


    public static List<String> splitExcelString(String line, String sep) {
        List<String> res = new ArrayList<String>(50);

        splitExcelString(line, sep, res);

        return res;
    }

    public static String hashStringSHA1(String pass) {
        String digestAlg = "SHA1";
        StringBuffer passhash = null;
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlg);

            byte[] digest = md.digest(pass.toString().getBytes());

            passhash = new StringBuffer(40);
            passhash.append(digestAlg).append('.');

            for (int i = 0; i < digest.length; i++) {
                passhash.append(Integer.toHexString(digest[i] & 0xFF));
            }

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA1 algorithm is not supported by your Java implementation");
        }

        return passhash.toString();
    }

    public static String toHexStr(byte[] dgst) {
        if (dgst == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (byte b : dgst) {
            int hxd = (b >> 4) & 0x0F;

            sb.append((char) (hxd >= 10 ? ('A' + (hxd - 10)) : ('0' + hxd)));

            hxd = b & 0x0F;

            sb.append((char) (hxd >= 10 ? ('A' + (hxd - 10)) : ('0' + hxd)));
        }

        return sb.toString();
    }

    public static int compareStrings(String s1, String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (s2 == null) {
                return 1;
            } else {
                return s1.compareTo(s2);
            }
        }
    }

    public static String readUnicodeFile(File f) throws IOException {
        ByteArrayOutputStream bais = new ByteArrayOutputStream();

        FileInputStream fis = new FileInputStream(f);
        StreamPump.doPump(fis, bais, false);
        fis.close();

        bais.close();

        byte[] barr = bais.toByteArray();
        String enc = "UTF-8";

        if (barr.length >= 2 && (barr[0] == -1 && barr[1] == -2) || (barr[0] == -2 && barr[1] == -1)) {
            enc = "UTF-16";
        }

        return new String(bais.toByteArray(), enc);
    }

    public static String readFully(InputStream is, Charset cs) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        StreamPump.doPump(is, os);

        if (cs == null) {
            cs = Charset.defaultCharset();
        }

        return new String(os.toByteArray(), cs);
    }

    public static String millisToString(long t) {
        StringBuilder sb = new StringBuilder();

        long frac = t / 3600000L;

        if (frac > 0) {
            sb.append(frac).append("h");
            t = t - frac * 3600000;
        }

        frac = t / 60000;

        if (frac > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }

            sb.append(frac).append("m");
            t = t - frac * 60000;
        }

        frac = t / 1000;

        if (frac > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }

            sb.append(frac).append("s");
            t = t - frac * 1000;
        }

        if (t > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }

            sb.append(t).append("ms");
        }

        return sb.toString();
    }


    public static List<String> splitString(String str, char sep) {
        List<String> res = new ArrayList<String>();

        int ptr = 0;
        int len = str.length();

        while (true) {
            if (ptr == len) {
                res.add("");
                break;
            }

            int pos = str.indexOf(sep, ptr);

            if (pos == -1) {
                if (res.size() == 0) {
                    res.add(str);
                } else {
                    res.add(str.substring(ptr));
                }

                break;
            }

            res.add(str.substring(ptr, pos));
            ptr = pos + 1;
        }

        return res;
    }

    public static List<String> splitEscapedString(String str, char sep, char esc, int maxPart) {
        List<String> res = new ArrayList<String>();

        if (maxPart == 1) {
            res.add(str);
            return res;
        }

        int ptr = 0;
        int beg = 0;
        int len = str.length();

        while (true) {
            if (beg == len) {
                res.add("");
                break;
            }

            int pos = str.indexOf(sep, ptr);

            if (pos == -1) {
                if (res.size() == 0) {
                    res.add(str);
                } else {
                    res.add(str.substring(beg));
                }

                break;
            }

            int nEcs = 0;
            for (int i = pos - 1; i >= 0; i--) {
                if (str.charAt(i) == esc) {
                    nEcs++;
                } else {
                    break;
                }
            }

            if (nEcs % 2 == 0) // even number of escapes
            {
                res.add(str.substring(beg, pos));

                if (maxPart > 0 && res.size() == maxPart - 1) {
                    res.add(pos + 1 < len ? str.substring(pos + 1) : "");
                    break;
                }

                beg = pos + 1;
            }

            ptr = pos + 1;

        }

        return res;
    }

    public static void xmlEscaped(String s, Appendable out) throws IOException {
        if (s == null) {
            return;
        }

        int len = s.length();

        boolean escaping = false;

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);

            if (ch < 0x20 && ch != 0x0D && ch != 0x0A && ch != 0x09) {

                if (!escaping) {
                    out.append(s.substring(0, i));
                    escaping = true;
                }

                // These characters are invalid in a XML document. Just ommiting them.

//    int rem = ch%16;
//    
//    out.append("&#").append( (ch > 15)?'1':'0' ).append( (char)(rem > 9?(rem-10+'A'):(rem+'0')) ).append(';');
            } else {
                boolean replaced = false;

                for (ReplacePair p : htmlPairs) {
                    if (ch == p.getSubject()) {
                        if (!escaping) {
                            out.append(s.substring(0, i));
                            escaping = true;
                        }

                        out.append(p.getReplacement());
                        replaced = true;
                        break;
                    }
                }

                if (!replaced) {
                    if (escaping) {
                        out.append(ch);
                    }
                }
            }
        }

        if (!escaping) {
            out.append(s);
        }
    }


    public static String removeEscapes(String str, char esc) {
        int start = 0;
        int pos = str.indexOf(esc);

        StringBuilder sb = null;

        while (pos != -1) {
            if (sb == null) {
                sb = new StringBuilder(str.length());
            }

            sb.append(str.substring(start, pos));

            start = pos + 1;

            if (start < str.length()) {
                sb.append(str.charAt(start));
            } else {
                break;
            }

            start++;

            pos = str.indexOf(esc, start);
        }

        if (sb == null) {
            return str;
        }

        sb.append(str.substring(start));

        return sb.toString();
    }

    public static String removeEscapes(String str, String esc) {
        int start = 0;
        int pos = str.indexOf(esc);

        StringBuilder sb = null;

        while (pos != -1) {
            if (sb == null) {
                sb = new StringBuilder(str.length());
            }

            sb.append(str.substring(start, pos));

            start = pos + esc.length();

            if (start < str.length()) {
                sb.append(str.charAt(start));
            } else {
                break;
            }

            start++;

            pos = str.indexOf(esc, start);
        }

        if (sb == null) {
            return str;
        }

        sb.append(str.substring(start));

        return sb.toString();
    }

    public static String stripLeadingSlashes(String str) {
        int l = str.length();
        int n = 0;

        while (n < l) {
            char ch = str.charAt(n);

            if (ch != '/' && ch != '\\') {
                break;
            }

            n++;
        }

        if (n == 0) {
            return str;
        }

        if (n == l) {
            return "";
        }

        return str.substring(n);
    }
}
