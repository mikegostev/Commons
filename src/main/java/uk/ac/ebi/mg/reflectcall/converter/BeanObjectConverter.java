package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import uk.ac.ebi.mg.reflectcall.ConverterFactory;
import uk.ac.ebi.mg.reflectcall.ConvertionException;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;

import com.pri.util.StringUtils;

public class BeanObjectConverter implements String2ValueConverter
{
 private ConverterFactory factory;
 
 public BeanObjectConverter(ConverterFactory standardConvertorFactory)
 {
  // TODO Auto-generated constructor stub
 }

 @Override
 public Object convert(String val, Type targetClass) throws ConvertionException
 {
  List<String> parts = StringUtils.splitString(val.substring(2, val.length() - 1), val.charAt(1));

  Object bean = null;

  try
  {
   bean = ((Class< ? >) targetClass).newInstance();
  }
  catch(InstantiationException | IllegalAccessException e)
  {
   throw new ConvertionException("Can't create object instance", e);
  }

  for(String s : parts)
  {
   int pos = s.indexOf('=');

   if(pos == -1)
    throw new ConvertionException("Invalid initializer string");

   String pval = s.substring(pos+1);
   
   String methName = "set"+Character.toUpperCase(s.charAt(0)) + s.substring(1, pos);

   Method setter = null;
   
   String2ValueConverter paramConv = null;
   
   try
   {
    setter = bean.getClass().getMethod(methName, String.class);
    paramConv = StringConverter.getInstance();
   }
   catch(NoSuchMethodException | SecurityException e)
   {
   }
   
   if( setter == null )
   {
    for ( Method m : bean.getClass().getMethods() )
    {
     if( methName.equals(m.getName()) && m.getParameterTypes().length == 1 )
     {
      paramConv = factory.getConverter(m.getParameterTypes()[0], pval);
      setter = m;
      
      break;
     }
    }
   }
  }
 }
}
