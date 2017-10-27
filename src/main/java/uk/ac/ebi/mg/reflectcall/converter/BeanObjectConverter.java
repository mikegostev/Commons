package uk.ac.ebi.mg.reflectcall.converter;

import com.pri.util.StringUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import uk.ac.ebi.mg.reflectcall.ConverterFactory;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public class BeanObjectConverter implements String2ValueConverter {

    private final ConverterFactory factory;

    public BeanObjectConverter(ConverterFactory f) {
        factory = f;
    }

    @Override
    public Object convert(String val, Type targetClass) throws ConvertionException {
        List<String> parts = StringUtils.splitString(val.substring(2, val.length() - 1), val.charAt(1));

        Object bean = null;

        try {
            bean = ((Class<?>) targetClass).newInstance();
        } catch (Exception e) {
            throw new ConvertionException("Can't create object instance for class " + targetClass, e);
        }

        for (String s : parts) {
            int pos = s.indexOf('=');

            if (pos == -1) {
                throw new ConvertionException("Invalid initializer string");
            }

            String pval = s.substring(pos + 1);

            String methName = "set" + Character.toUpperCase(s.charAt(0)) + s.substring(1, pos);

            Method setter = null;

            String2ValueConverter paramConv = null;

            Type tgtType = null;

            try {
                setter = bean.getClass().getMethod(methName, String.class);
                paramConv = StringConverter.getInstance();
                tgtType = String.class;
            } catch (Exception e) {
            }

            if (setter == null) {
                for (Method m : bean.getClass().getMethods()) {
                    if (methName.equals(m.getName()) && m.getParameterTypes().length == 1) {
                        tgtType = m.getParameterTypes()[0];
                        paramConv = factory.getConverter(tgtType, pval);
                        setter = m;

                        break;
                    }
                }
            }

            try {
                setter.invoke(bean, paramConv.convert(pval, tgtType));
            } catch (Exception e) {
                throw new ConvertionException("Can't call setter method '" + methName + "' for class " + targetClass);
            }
        }

        return bean;
    }
}
