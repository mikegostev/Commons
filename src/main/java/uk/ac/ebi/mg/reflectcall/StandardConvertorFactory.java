package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.mg.reflectcall.converter.ArrayConverter;
import uk.ac.ebi.mg.reflectcall.converter.BeanObjectConverter;
import uk.ac.ebi.mg.reflectcall.converter.CollectionConverter;
import uk.ac.ebi.mg.reflectcall.converter.ConstructorConverter;
import uk.ac.ebi.mg.reflectcall.converter.PrimitiveTypeConverter;
import uk.ac.ebi.mg.reflectcall.converter.StringConverter;

public class StandardConvertorFactory implements ConverterFactory
{
 private static StandardConvertorFactory instance;
 private static Map<Class<?>, String2ValueConverter> standardConv = new HashMap<Class<?>, String2ValueConverter>();

 public static StandardConvertorFactory getInstance()
 {
  if( instance == null )
   instance = new StandardConvertorFactory();
  
  return instance;
 }

 private Map<Class<?>, String2ValueConverter> customConv;
 
 public StandardConvertorFactory()
 {
 }

 public StandardConvertorFactory( Map<Class<?>, String2ValueConverter> cMap )
 {
  customConv = cMap;
 }
 
 @Override
 public String2ValueConverter getConverter(Class< ? > cls, String value)
 {
  if(cls == String.class)
   return StringConverter.getInstance();
  else
  {
   String2ValueConverter conv = null;

   if(customConv != null)
    conv = customConv.get(cls);

   if(conv == null)
    conv = standardConv.get(cls);

   if(conv == null && cls.isPrimitive())
    conv = PrimitiveTypeConverter.getInstance();

   if(conv == null && cls.isArray())
    conv = new ArrayConverter( this );

   if( Collection.class.isAssignableFrom(cls) )
    conv = new CollectionConverter( this );
   
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
     && value.length() > 2 && value.charAt(0) == hashBrackets.charAt(0))
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

  return null;
 }

 
 
}
