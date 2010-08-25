package com.pri.util;

public class M2codec
{
 private static final char escChar='_';
 
 private M2codec()
 {
 }
 
 public static String encode( String str, String escChars )
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
    sb.append(escChar).append((char)('A' + (ch >> 4 & 0x0F))).append((char)('A' + (ch & 0x0F)));
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
  
  int pos = str.indexOf(escChar);


  StringBuilder sb = new StringBuilder(str.length());

  if(pos > 0)
   sb.append(str.substring(0, pos));

  do
  {
   char ch = str.charAt(pos);

   if(ch == escChar)
   {
    sb.append((char) (((str.charAt(pos+1) - 'A') << 4) + (str.charAt(pos+2) - 'A')));
    pos+=2;
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
