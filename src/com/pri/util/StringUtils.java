/*
 * Created on 24.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;



/**
 * @author mg
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StringUtils
{
 private static class CharPair implements Comparable<CharPair>
 {
  char first;
  char second;
  
  public CharPair()
  {}
  
  public CharPair(char first, char second)
  {
   this.first = first;
   this.second = second;
  }
  
  public char getFirst()
  {
   return first;
  }
  
  public void setFirst(char first)
  {
   this.first = first;
  }
  
  public char getSecond()
  {
   return second;
  }

  public void setSecond(char second)
  {
   this.second = second;
  }

  public int compareTo(CharPair toCmp)
  {
   return first-toCmp.first;
  }
  
  public boolean equals( Object o )
  {
   return first==((CharPair)o).first;
  }
  
 }
 
 static final char[] htmlEscChars = new char[]{  '"',     '\'',   '<',   '>',   '&'};
 static final String[] htmlEntity = new String[]{"&quot;","&#39;","&lt;","&gt;","&amp;"};
 static final char[] escChars = {'\'','"','\\','\0'};
 
 static final char[][] escPairsO = {{'\'','\''},{'"','"'},{'\\','\\'},{'\0','0'},{'\n','n'},{'\r','r'}};
 static final CharPair[] escPairs = { 
  new CharPair('\'','\''),
  new CharPair('"','"'),
  new CharPair('\\','\\'),
  new CharPair('\0','0'),
  new CharPair('\n','n'),
  new CharPair('\r','r')
  };
 
 static final Comparator<char[]> pairComp = new Comparator<char[]>()
 {
  public int compare(char[] arg0, char[] arg1)
  {
   return arg0[0]-arg1[0];
  }
 };

 static
 {
  Arrays.sort( escPairs );
 }
 /**
  * 
  */
 private StringUtils()
 {}

 public static int naturalCompare(String str1, String str2)
 {
  int l1 = str1.length();
  int l2 = str2.length();
  
  int ml = l1<l2?l1:l2;
  
  int v1=0, v2=0;

  int i=0;
  
  for( ; i < ml && str1.charAt(i) == str2.charAt(i) && ( ! Character.isDigit(str1.charAt(i)) ) && ( ! Character.isDigit(str2.charAt(i)) ) ; i++ );

  int j=i;
  for( ; j < l1 && Character.isDigit(str1.charAt(j)); j++ )
   v1 = v1*10+(str1.charAt(j)-'0');

  int k=i;
  for( ; k < l2 && Character.isDigit(str2.charAt(k)); k++ )
   v2 = v2*10+(str2.charAt(k)-'0');
  
  if( v1==0 && v2==0 )
  {
   if( i < l1 )
   {
    if( i < l2 )
     return str1.charAt(i)-str2.charAt(i);
    else
     return 1;
   }
   else if( i == l2 )
    return l1-l2;
   else
    return -1;
  }
  else if( v1 == v2 )
  {
   if( j < l1 )
   {
    if( k < l2 )
     return naturalCompare(str1.substring(j), str2.substring(k) );
    else
     return 1;
   }
   else if( k < l2 )
    return -1;
   else
    return l1-l2;
  }
  else
   return v1-v2;
 }
 
 /**
  * Adds backslashes before every char ch
  * 
 */
 public static StringBuffer appendEscaped( StringBuffer sb, String str, char ch )
 {
  return appendEscaped( sb, str, ch, '\\');
 }

 public static StringBuffer appendEscaped( StringBuffer sb, String str, char ch, char escChar )
 {
  int cPos,ePos;
  
  cPos=0;
  while( cPos < str.length() )
  {
   ePos=str.indexOf(ch,cPos);
    
   if( ePos == -1 )
   {
    if( cPos == 0)
     sb.append(str);
    else
     sb.append(str.substring(cPos));
    
    return sb;
   }
   
   sb.append(str.substring(cPos,ePos));
   sb.append(escChar);
   sb.append(ch);
   
   cPos=ePos+1;
  }
 
  return sb;
 }
 /**
  * Adds backslashes before every char ch
  * 
 */
 public static StringBuilder appendEscaped( StringBuilder sb, String str, char ch )
 {
  return appendEscaped( sb, str, ch, '\\');
 }

 public static StringBuilder appendEscaped( StringBuilder sb, String str, char ch, char escChar )
 {
  int cPos,ePos;

  cPos=0;
  while( cPos < str.length() )
  {
   ePos=str.indexOf(ch,cPos);
    
   if( ePos == -1 )
   {
    if( cPos == 0)
     sb.append(str);
    else
     sb.append(str.substring(cPos));
    
    return sb;
   }
   
   sb.append(str.substring(cPos,ePos));
   sb.append(escChar);
   sb.append(ch);
   
   cPos=ePos+1;
  }
 
  return sb;
 }

 public static StringBuilder appendSlashed( StringBuilder sb, String str )
 {
  return escape( sb, str, escPairs );
 }

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

 public static String jsString( String str )
 {
  if( str == null )
   return "null";
  
  StringBuilder sb = new StringBuilder( str.length() + 10 );
  
  sb.append('\'');
  escape(sb, str, escPairs);
  sb.append('\'');
  
  return sb.toString();
 }
 
 public static String addSlashes( String str, char ch )
 {
  StringBuffer sb=null;
  int cPos,ePos;
  
  cPos=0;
  while( cPos < str.length() )
  {
   ePos=str.indexOf(ch,cPos);
    
   if( ePos == -1 )
   {
    if( sb == null )
     return str;

    sb.append(str.substring(cPos));
     return sb.toString();

   }
   
   if( sb == null )
    sb=new StringBuffer(str.length()*2);
    
   sb.append(str.substring(cPos,ePos));
   sb.append('\\');
   sb.append(ch);
   
   cPos=ePos+1;
  }
 
  if( sb != null )
   return sb.toString();
  
  return str;
 }
 
 
 public static String addSlashes( String str, String escChars )
 {
  StringBuffer sb=null;
  int cPos,ePos,mPos;
  
  cPos=0;
  while( cPos < str.length() )
  {
   ePos = Integer.MAX_VALUE;
   for( int i=0; i < escChars.length(); i++ )
   {
    mPos=str.indexOf(escChars.charAt(i),cPos);
    
    if( mPos == -1 )
     continue;
    
    if( mPos < ePos )
     ePos=mPos;
    
   }
   
   if( ePos == Integer.MAX_VALUE )
   {
    if( sb == null )
     return str;

    sb.append(str.substring(cPos));
     return sb.toString();
   }
   
   if( sb == null )
    sb=new StringBuffer(str.length()*2);
    
   sb.append(str.substring(cPos,ePos));
   sb.append('\\');
   sb.append(str.charAt(ePos));
   
   cPos=ePos+1;
  }
 
  if( sb != null )
   return sb.toString();
  
  return str;
 }

 public static String htmlEscaped( String s )
 {
  if( s == null )
   return "";
  
  StringBuilder sb = null;
  
  int l=s.length();
  int ne = htmlEscChars.length;
  ext: for( int i=0; i < l; i++ )
  {
   char ch = s.charAt(i);
   for( int j=0; j < ne; j++ )
   {
    if( htmlEscChars[j] == ch )
    {
     if( sb == null )
     {
      sb = new StringBuilder(l*2);
      if( i > 0 )
       sb.append( s.substring(0, i) );
     }
     
     sb.append(htmlEntity[j]);
     continue ext;
    }
   }
   
   if( sb != null )
    sb.append(ch);
  }
  
  return sb!=null?sb.toString():s;
 }
 
 public static void splitExcelString(String line, String sep, List<String> accum)
 {
  int spos;
  StringBuilder sb=null;
  
  while( line.length() > 0 )
  {
   
   if( line.charAt(0) != '"' ) // This cell is not quoted
   {
    spos = line.indexOf(sep);
    
    if( spos < 0 )
    {
     accum.add(line);
     return;
    }
    
    accum.add(line.substring(0,spos));
    line = line.substring(spos+sep.length());
   }
   else
   {
    int qpos;
    int beg = 1;
    
    while( true ) // A quoted cell can contain double quote inside. Let'd build the cell content from parts
    {
     qpos = line.indexOf('"',beg);
     
     if( qpos == -1 ) // actually this is the erroneous situation - quoted part is not finished by the quotation symbol. 
     {
      if( sb != null && sb.length() > 0 )
      {
       sb.append(line.substring(beg));
       accum.add(sb.toString());
      }
      else
       accum.add(line.substring(beg));
      
      return;
     }
     else if( qpos == line.length()-1 )
     {
      if( sb != null && sb.length() > 0 )
      {
       sb.append(line.substring(beg,line.length()-1));
       accum.add(sb.toString());
      }
      else
       accum.add(line.substring(beg,line.length()-1));

      return;
     }
     
     if( line.charAt(qpos+1) == '"' ) // We have found a double quote
     {
      if( sb == null )
       sb = new StringBuilder(200);
      
      sb.append(line.substring(beg, qpos+1)); // adding part of the cell to the buffer and continue
      beg = qpos+2;
     }
     else
     {
      if( line.startsWith(sep, qpos+1) )
      {
       if( sb != null && sb.length() > 0 )
       {
        sb.append(line.substring(beg, qpos) );
        accum.add(sb.toString());
        sb.setLength(0);
       }
       else
        accum.add(line.substring(beg, qpos));
        
       line=line.substring(qpos+sep.length()+1);
       break;
      }
      else // actually this is the erroneous situation - quotation symbol have to be followed by separator or to be doubled . 
      {
       if( sb == null )
        sb = new StringBuilder(200);
       
       sb.append(line.substring(beg, qpos+1));
       beg = qpos+1;
      }
     }
     
    }
   }
  }
 }

 
 public static List<String> splitExcelString(String line, String sep)
 {
  List<String> res = new ArrayList<String>(50);
  
  splitExcelString(line, sep, res);
  
  return res;
 }

}
