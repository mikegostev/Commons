package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import uk.ac.ebi.mg.reflectcall.converter.ArrayConverter;
import uk.ac.ebi.mg.reflectcall.converter.BeanObjectConverter;
import uk.ac.ebi.mg.reflectcall.converter.CollectionConverter;
import uk.ac.ebi.mg.reflectcall.converter.ConstructorConverter;
import uk.ac.ebi.mg.reflectcall.converter.FabMethodConverter;
import uk.ac.ebi.mg.reflectcall.converter.PrimitiveTypeConverter;
import uk.ac.ebi.mg.reflectcall.converter.StringConverter;

public class StandardConverterFactory implements ConverterFactory {

    private static StandardConverterFactory instance;
    private static Map<Class<?>, String2ValueConverter> standardConv = new HashMap<Class<?>, String2ValueConverter>();

    public static StandardConverterFactory getInstance() {
        if (instance == null) {
            instance = new StandardConverterFactory();
        }

        return instance;
    }

    private Map<Class<?>, String2ValueConverter> customConv;

    public StandardConverterFactory() {
    }

    public StandardConverterFactory(Map<Class<?>, String2ValueConverter> cMap) {
        customConv = cMap;
    }

    @Override
    public String2ValueConverter getConverter(Type typ, String value) {
        if (typ == String.class) {
            return StringConverter.getInstance();
        }

        Class<?> cls = null;

        if (typ instanceof Class) {
            cls = (Class<?>) typ;
        } else if (typ instanceof ParameterizedType) {
            cls = (Class<?>) ((ParameterizedType) typ).getRawType();
        }

        String2ValueConverter conv = null;

        if (customConv != null) {
            conv = customConv.get(cls);
        }

        if (conv == null) {
            conv = standardConv.get(cls);
        }

        if (conv == null && cls.isPrimitive()) {
            conv = PrimitiveTypeConverter.getInstance();
        }

        if (conv == null && cls.isArray()) {
            conv = new ArrayConverter(this);
        }

        if (Collection.class.isAssignableFrom(cls)) {
            conv = new CollectionConverter(this);
        }

        if (conv != null) {
            return conv;
        }

        //   {
        //    try
        //    {
        //     params[i] = conv.convert(input[i+1], cls);
        //     continue;
        //    }
        //    catch(ConvertionException e)
        //    {
        //     throw new ArgumentConversionException("Argument #"+i+" conversion error. Target class: "+cls.getName()
        // +". "+e.getMessage(), i);
        //    }
        //   }

        if (!value.startsWith(bracketOverridePrefix) && value.charAt(value.length() - 1) == hashBrackets.charAt(1)
                && value.length() > 2 && value.charAt(0) == hashBrackets.charAt(0)) {
            return new BeanObjectConverter(this);
        }

        Method fabMeth = null;

        try {
            fabMeth = cls.getMethod(fabricMethodName, String.class);

            if (!Modifier.isStatic(fabMeth.getModifiers()) || !cls.isAssignableFrom(fabMeth.getReturnType())) {
                fabMeth = null;
            }
        } catch (Exception e1) {
        }

        if (fabMeth != null) {
            return new FabMethodConverter(fabMeth);
        }

        Constructor<?> ctor = null;

        try {
            ctor = cls.getConstructor(String.class);
        } catch (Exception e) {
        }

        if (ctor != null) {
            return new ConstructorConverter(ctor);
        }

        return null;
    }


}
