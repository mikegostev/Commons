package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import uk.ac.ebi.mg.reflectcall.ConvertionException;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;

public class FabMethodConverter implements String2ValueConverter
{
 private final Method fabMeth;

 public FabMethodConverter(Method fabMeth)
 {
  this.fabMeth = fabMeth;
 }

 @Override
 public Object convert(String val, Type targetType) throws ConvertionException
 {
  try
  {
   return fabMeth.invoke(null, val) ;
  }
  catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
  {
   throw new ConvertionException("Fabric method call error: "+e1.getMessage()+") Target type: "+targetType);
  }
 }

}
