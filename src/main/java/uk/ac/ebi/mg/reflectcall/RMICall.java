package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RMICall
{
 
 private static Map<Class<?>, OutputFormatter> formatters = new HashMap<Class<?>, OutputFormatter>();

 static
 {
 }
 
 public static String call(Object instance, String... input) throws AmbiguousMethodCallException,
 MethodNotExistException, ArgumentConversionException,
 InvocationTargetException, MethodInvocationException
 {
  return call(instance, null, null, input);
 }

 public static String call(Object instance, Map<Class<?>, String2ValueConverter> cConv, Map<Class<?>, OutputFormatter> cFmt, String... input) throws AmbiguousMethodCallException,
   MethodNotExistException, ArgumentConversionException,
   InvocationTargetException, MethodInvocationException
 {
  
  String methodName = input[0];
  
  Method method = null;
  
  for( Method mth : instance.getClass().getMethods() )
  {
   
   if( mth.getName().equals(methodName) && mth.getParameterTypes().length == input.length-1 )
   {
    if( method != null )
     throw new AmbiguousMethodCallException();
    
    method = mth;
   }
   
  }
  
  if( method == null )
   throw new MethodNotExistException();
  
  Object[] params = new Object[ input.length - 1 ];
  
  int i=-1;
  
  for( Type typ : method.getGenericParameterTypes() )
  {
   i++;
   
   String2ValueConverter conv = new StandardConverterFactory(cConv).getConverter(typ, input[i+1]);
   
   try
   {
    params[i] = conv.convert(input[i+1], typ);
   }
   catch(ConvertionException e)
   {
    throw new ArgumentConversionException("Argument #"+i+" conversion error. Target type: "+typ+". "+e.getMessage(), i);
   }
   
  }
  
  Object val = null;
  
  try
  {
   val = method.invoke(instance, params);
  }
  catch( Exception e)
  {
   throw new MethodInvocationException("Invocation error",e);
  }
  
  if( val == null )
   return null;
 
  Class<?> retClass = method.getReturnType();
  
  OutputFormatter fmt = null;
  
  if( cFmt != null )
   fmt = cFmt.get( retClass );
  
  if( fmt == null )
   fmt = formatters.get(retClass);
   
  if( fmt != null )
   try
   {
    return fmt.format(val, retClass);
   }
   catch(FormatterException e1)
   {
    return null;
   }

  
  if(retClass.isPrimitive())
  {
   try
   {
    Method valueOfMeth = String.class.getMethod("valueOf", retClass);
    return (String) valueOfMeth.invoke(null, val);
   }
   catch( Exception e)
   {
    throw new MethodInvocationException("Can't invoke String.valueOf method for arg: " + retClass.getName(), e);
   }

  }
  
  if( retClass.isArray() )
  {
   Class<?> arrClass = retClass.getComponentType();
   
   if( cFmt != null )
    fmt = cFmt.get( arrClass );
   
   if( fmt == null )
    fmt = formatters.get(arrClass);
    
   StringBuilder sb = new StringBuilder();
   int len = Array.getLength(val);

   if( fmt != null )
   {
    
    for(int j = 0; j < len; j++)
     try
     {
      sb.append( fmt.format(Array.get(val, j), null)).append('\n');
     }
     catch( Exception e)
     {
      return null;
     }
    
    return sb.toString();
   }
   else if( arrClass.isPrimitive() )
   {
    Method valueOfMeth = null;
    
    try
    {
     valueOfMeth = String.class.getMethod("valueOf", retClass);
    }
    catch( Exception e)
    {
     throw new MethodInvocationException("Can't find String.valueOf method for arg: " + retClass.getName(), e);
    }

    try
    {
     for(int j = 0; j < len; j++)
      sb.append(valueOfMeth.invoke(null, Array.get(val, j))).append('\n');
    }
    catch( Exception e)
    {
     throw new MethodInvocationException("Can't invoke String.valueOf method for arg: " + retClass.getName(), e);
    }
    
    return sb.toString();
   }
   else
    for(int j = 0; j < len; j++)
     sb.append(Array.get(val, j).toString()).append('\n');
  }
  
  return val.toString();
 } 
}
