package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import uk.ac.ebi.mg.reflectcall.exception.AmbiguousMethodCallException;
import uk.ac.ebi.mg.reflectcall.exception.ArgumentConversionException;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;
import uk.ac.ebi.mg.reflectcall.exception.FormatterException;
import uk.ac.ebi.mg.reflectcall.exception.MethodInvocationException;
import uk.ac.ebi.mg.reflectcall.exception.MethodNotExistException;

public class ReflectionCall {

    public static String call(Object instance, String... input)
            throws AmbiguousMethodCallException, MethodNotExistException, ArgumentConversionException,
            InvocationTargetException, MethodInvocationException, FormatterException {
        return call(instance, null, null, input);
    }

    public static String call(Object instance, Map<Class<?>, String2ValueConverter> cConv,
            Map<Class<?>, OutputFormatter> cFmt, String... input)
            throws AmbiguousMethodCallException, MethodNotExistException, ArgumentConversionException,
            MethodInvocationException, FormatterException {

        String methodName = input[0];

        Method method = null;

        for (Method mth : instance.getClass().getMethods()) {

            if (mth.getName().equals(methodName) && mth.getParameterTypes().length == input.length - 1) {
                if (method != null) {
                    throw new AmbiguousMethodCallException();
                }

                method = mth;
            }

        }

        if (method == null) {
            throw new MethodNotExistException();
        }

        Object[] params = new Object[input.length - 1];

        int i = -1;

        for (Type typ : method.getGenericParameterTypes()) {
            i++;

            String2ValueConverter conv = new StandardConverterFactory(cConv).getConverter(typ, input[i + 1]);

            if (conv == null) {
                throw new ArgumentConversionException(
                        "Argument #" + i + " conversion error. Can't find converter for type: " + typ, i);
            }

            try {
                params[i] = conv.convert(input[i + 1], typ);
            } catch (ConvertionException e) {
                throw new ArgumentConversionException(
                        "Argument #" + i + " conversion error. Target type: " + typ + ". " + e.getMessage(), i);
            }

        }

        Object val = null;

        try {
            val = method.invoke(instance, params);
        } catch (Exception e) {
            throw new MethodInvocationException("Invocation error", e);
        }

        if (val == null) {
            return null;
        }

        Class<?> retClass = method.getReturnType();

        OutputFormatter fmt = new StandardFormatterFactory(cFmt).getFormatter(retClass);

        return fmt.format(val, retClass);
    }
}
