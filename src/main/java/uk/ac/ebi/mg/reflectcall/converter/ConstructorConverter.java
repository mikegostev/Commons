package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import uk.ac.ebi.mg.reflectcall.ConvertionException;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;

public class ConstructorConverter implements String2ValueConverter
{
 private final Constructor< ? > constructor;
 
 public ConstructorConverter(Constructor< ? > ctor)
 {
  constructor = ctor;
 }

 @Override
 public Object convert(String val, Class< ? > targetClass) throws ConvertionException
 {
  try
  {
   return constructor.newInstance(val);
  }
  catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
  {
   throw new ConvertionException("Constructor call error: "+e.getMessage()+") Target class: "+targetClass.getName());
  }
 }

}
