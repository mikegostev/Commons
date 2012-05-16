package uk.ac.ebi.mg.packedstring;

import uk.ac.ebi.mg.packedstring.DualBandString.PackingImpossibleException;


public class PackedString
{

 private PackedString()
 {}
 
 public static Object pack( String str )
 {
  int len = str.length();
  
  if( len < 10 )
   return str;
  
  char top, bottom;
  
 
  bottom=top=str.charAt(0);
  
  for( int i=1; i < len; i++ )
  {
   char ch = str.charAt(i);
   
   if( ch < bottom )
    bottom = ch;
   else if( ch > top )
    top = ch;
  }
  
  if( top - bottom < 256 )
   return new SingleBandString(str, bottom);
  
  try
  {
   return new DualBandString(str, bottom, top );
  }
  catch(PackingImpossibleException e)
  {
  }
  
  return str;
 }
 
 
 
}
