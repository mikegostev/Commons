package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.mg.reflectcall.converter.ArrayConverter;
import uk.ac.ebi.mg.reflectcall.converter.BeanObjectConverter;
import uk.ac.ebi.mg.reflectcall.converter.ConstructorConverter;
import uk.ac.ebi.mg.reflectcall.converter.PrimitiveTypeConverter;
import uk.ac.ebi.mg.reflectcall.converter.StringConverter;

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
  
  for( Class<?> cls : method.getParameterTypes() )
  {
   i++;
   
   if( cls == String.class )
    params[i] = input[i+1];
   else
   {
    String2ValueConverter conv = null;
    
    if( cConv != null )
     conv = cConv.get(cls);
    
    if( conv == null )
     conv = directConverterMap.get(cls);

    if( conv == null &&  cls.isPrimitive() )
     conv = PrimitiveTypeConverter.getInstance();
    
    if( conv == null &&  cls.isArray() )
     conv = ArrayConverter.getInstance();    
    
    if( conv != null )
    {
     try
     {
      params[i] = conv.convert(input[i+1], cls);
      continue;
     }
     catch(ConvertionException e)
     {
      throw new ArgumentConversionException("Argument #"+i+" conversion error. Target class: "+cls.getName()+". "+e.getMessage(), i);
     }
    }
    
    Method fabMeth = null;
    
    try
    {
     fabMeth = cls.getMethod(fabricMethodName, String.class);
     
     if( ! Modifier.isStatic( fabMeth.getModifiers() ) || ! cls.isAssignableFrom(fabMeth.getReturnType()) )
      fabMeth = null;
    }
    catch(NoSuchMethodException | SecurityException e1)
    {
    }
    
    if( fabMeth != null )
    {
     try
     {
      params[i] = fabMeth.invoke(null, input[i+1]) ;
      continue;
     }
     catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
     {
      throw new ArgumentConversionException("Argument #"+i+" conversion error. (Fabric method call error: "+e1.getMessage()+") Target class: "+cls.getName(), i);
     }
    }
    
    Constructor<?> ctor = null;

    try
    {
     ctor = cls.getConstructor(String.class);
    }
    catch(NoSuchMethodException | SecurityException e)
    {
    }

    if( ctor != null )
    {
     try
     {
      params[i] = ctor.newInstance(input[i+1]);
      continue;
     }
     catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
     {
      throw new ArgumentConversionException("Argument #"+i+" conversion error. (Constructor call error: "+e.getMessage()+") Target class: "+cls.getName(), i);
     }
    }
    
    throw new ArgumentConversionException("Argument #"+i+" conversion error. No corresponding converter. Target class: "+cls.getName(), i);

   }
  }
  
  Object val = null;
  
  try
  {
   val = method.invoke(instance, params);
  }
  catch(IllegalAccessException | IllegalArgumentException e)
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
   return fmt.format(val);

  
  if(retClass.isPrimitive())
  {
   try
   {
    Method valueOfMeth = String.class.getMethod("valueOf", retClass);
    return (String) valueOfMeth.invoke(null, val);
   }
   catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e)
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
     sb.append( fmt.format(Array.get(val, j))).append('\n');
    
    return sb.toString();
   }
   else if( arrClass.isPrimitive() )
   {
    Method valueOfMeth = null;
    
    try
    {
     valueOfMeth = String.class.getMethod("valueOf", retClass);
    }
    catch(NoSuchMethodException | SecurityException | IllegalArgumentException e)
    {
     throw new MethodInvocationException("Can't find String.valueOf method for arg: " + retClass.getName(), e);
    }

    try
    {
     for(int j = 0; j < len; j++)
      sb.append(valueOfMeth.invoke(null, Array.get(val, j))).append('\n');
    }
    catch(ArrayIndexOutOfBoundsException | IllegalAccessException | IllegalArgumentException e)
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
 
 private static String2ValueConverter getConverter( Class<?> cls, String value, Map<Class<?>, String2ValueConverter> cConv)
 {
  if(cls == String.class)
   return StringConverter.getInstance();
  else
  {
   String2ValueConverter conv = null;

   if(cConv != null)
    conv = cConv.get(cls);

   if(conv == null)
    conv = directConverterMap.get(cls);

   if(conv == null && cls.isPrimitive())
    conv = PrimitiveTypeConverter.getInstance();

   if(conv == null && cls.isArray())
    conv = ArrayConverter.getInstance();

   if(conv != null)
    return conv;

   //   {
   //    try
   //    {
   //     params[i] = conv.convert(input[i+1], cls);
   //     continue;
   //    }
   //    catch(ConvertionException e)
   //    {
   //     throw new ArgumentConversionException("Argument #"+i+" conversion error. Target class: "+cls.getName()+". "+e.getMessage(), i);
   //    }
   //   }

   if(!value.startsWith(bracketOverridePrefix) && value.charAt(value.length() - 1) == hashBrackets.charAt(1)
     && value.length() > bracketOverridePrefix.length() + 2 && value.charAt(bracketOverridePrefix.length()) == hashBrackets.charAt(0))
    return new BeanObjectConverter();

   Method fabMeth = null;

   try
   {
    fabMeth = cls.getMethod(fabricMethodName, String.class);

    if(!Modifier.isStatic(fabMeth.getModifiers()) || !cls.isAssignableFrom(fabMeth.getReturnType()))
     fabMeth = null;
   }
   catch(NoSuchMethodException | SecurityException e1)
   {
   }

   if(fabMeth != null)
    return new FabMethodConverter(fabMeth);

   Constructor< ? > ctor = null;

   try
   {
    ctor = cls.getConstructor(String.class);
   }
   catch(NoSuchMethodException | SecurityException e)
   {
   }

   if(ctor != null)
    return new ConstructorConverter(ctor);

  }

 }
 
}
