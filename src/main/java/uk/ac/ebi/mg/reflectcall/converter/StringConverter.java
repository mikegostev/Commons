package uk.ac.ebi.mg.reflectcall.converter;

import uk.ac.ebi.mg.reflectcall.ConvertionException;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;

public class StringConverter implements String2ValueConverter
{
 private static StringConverter instance;

 @Override
 public Object convert(String val, Class< ? > targetClass) throws ConvertionException
 {
  return val;
 }

 public static String2ValueConverter getInstance()
 {
  if( instance == null )
   instance = new StringConverter();
  
  return instance;
 }

}
