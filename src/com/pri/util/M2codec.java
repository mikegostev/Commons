package com.pri.util;

public class M2codec
{
 private static final char escCharLo='_';
 private static final char escCharHi='-';
 
 private M2codec()
 {
 }
 
 public static String encode( String str )
 {
  int pos = 0;
  char ch = 0;

  while(pos < str.length())
   if(!isCharLegal(ch = str.charAt(pos) ) )
    break;
   else
    pos++;

  if(pos == str.length())
   return str;

  StringBuilder sb = new StringBuilder(str.length() * 3 + 6);

  if(pos > 0)
   sb.append(str.substring(0, pos));

  do
  {
   ch = str.charAt(pos);
   if( !isCharLegal(ch) )
   {
    if( ch < 256) 
     sb.append(escCharLo).append((char)('A' + (ch >> 4 & 0x0F))).append((char)('A' + (ch & 0x0F)));
    else  
     sb.append(escCharHi).append((char)('A' + (ch >> 12 & 0x0F))).append((char)('A' + (ch >> 8 & 0x0F)))
     .append((char)('A' + (ch >> 4 & 0x0F))).append((char)('A' + (ch & 0x0F)));
   }
   else
    sb.append(ch);

   pos++;
  } while(pos < str.length());
  
  return sb.toString();
 }

 public static String decode(String str)
 {
  if( str == null )
   return null;
  
  int posL = str.indexOf(escCharLo);
  int posH = str.indexOf(escCharHi);

  StringBuilder sb = new StringBuilder(str.length());

  int pos=-1;
  
  if(posL >= 0)
  {
   if( posH >= 0 )
   {
    if( posL < posH )
     pos = posL;
    else
     pos = posH;
   }
   else
    pos=posL;
  }
  else
  {
   if( posH >= 0 )
    pos=posH;
   else
    return str;
  }
   
  sb.append(str.substring(0, pos));

  do
  {
   char ch = str.charAt(pos);

   if(ch == escCharLo )
   {
    sb.append((char) (((str.charAt(pos+1) - 'A') << 4) + (str.charAt(pos+2) - 'A')));
    pos+=2;
   }
   else if(ch == escCharHi )
   {
    sb.append((char) (((str.charAt(pos+1) - 'A') << 12) + ((str.charAt(pos+2) - 'A')<< 8)+((str.charAt(pos+3) - 'A') << 4) + (str.charAt(pos+4) - 'A')));
    pos+=4;
   }
   else
     sb.append(ch);

   pos++;
  } while(pos < str.length());
  
  return sb.toString();
 }
 
 private static boolean isCharLegal( char ch )
 {
  if( ( ch >= '0' && ch <= '9' ) || ( ch >= 'a' && ch <= 'z' ) || ( ch >= 'A' && ch <= 'Z') )
   return true;
  
  return false;
 }


}
