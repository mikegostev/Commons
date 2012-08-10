package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Method;

public class PrimitiveClassFormatter implements OutputFormatter
{

 @Override
 public String format(Object obj, Class<?> prClass) throws FormatterException
 {
  try
  {
   Method valueOfMeth = String.class.getMethod("valueOf",prClass);
   return (String) valueOfMeth.invoke(null, obj);
  }
  catch(Exception e)
  {
   throw new FormatterException("Can't invoke String.valueOf method for arg: " + prClass.getName(), e);
  }
 }

}
