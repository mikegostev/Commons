package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FabMethodConverter implements String2ValueConverter
{
 private final Method fabMeth;

 public FabMethodConverter(Method fabMeth)
 {
  this.fabMeth = fabMeth;
 }

 @Override
 public Object convert(String val, Class< ? > targetClass) throws ConvertionException
 {
  try
  {
   return fabMeth.invoke(null, val) ;
  }
  catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
  {
   throw new ConvertionException("Fabric method call error: "+e1.getMessage()+") Target class: "+targetClass.getName());
  }
 }

}
