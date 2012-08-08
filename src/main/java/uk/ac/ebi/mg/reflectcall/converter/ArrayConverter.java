package uk.ac.ebi.mg.reflectcall.converter;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.mg.reflectcall.ConverterFactory;
import uk.ac.ebi.mg.reflectcall.ConvertionException;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;

import com.pri.util.StringUtils;

public class ArrayConverter implements String2ValueConverter
{
 private final ConverterFactory convFactory;
 
 public ArrayConverter(ConverterFactory convFact)
 {
  convFactory=convFact;
 }


 @Override
 public Object convert(String val, Class< ? > targetClass) throws ConvertionException
 {
  // TODO Auto-generated method stub
  return null;
 }

 protected static List<String> splitString( String val )
 {
  if( val.startsWith(ConverterFactory.bracketOverridePrefix) )
  {
   ArrayList< String > res = new ArrayList< String >(1);
   res.add( val.substring(ConverterFactory.bracketOverridePrefix.length()) );
   return res;
  }
  
  if( val.charAt(val.length() - 1) == ConverterFactory.arrayBrackets.charAt(1)
    && val.length() > 2 && val.charAt(0) == ConverterFactory.arrayBrackets.charAt(0))
  {
   char sep = val.charAt(1);
   
   return StringUtils.splitString(val, sep);
  }
  
  ArrayList< String > res = new ArrayList< String >(1);
  res.add( val );

  return res;
 }
}
