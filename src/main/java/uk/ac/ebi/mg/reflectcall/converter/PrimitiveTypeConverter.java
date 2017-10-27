package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.Type;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public class PrimitiveTypeConverter implements String2ValueConverter {

    private static PrimitiveTypeConverter instance = new PrimitiveTypeConverter();

    public static PrimitiveTypeConverter getInstance() {
        return instance;
    }

    @Override
    public Object convert(String val, Type targetType) throws ConvertionException {
        Class<?> targetClass = (Class<?>) targetType;

        try {
            if (targetClass == int.class) {
                return Integer.parseInt(val);
            } else if (targetClass == long.class) {
                return Long.parseLong(val);
            } else if (targetClass == boolean.class) {
                return Boolean.parseBoolean(val);
            } else if (targetClass == short.class) {
                return Short.parseShort(val);
            } else if (targetClass == float.class) {
                return Float.parseFloat(val);
            } else if (targetClass == double.class) {
                return Double.parseDouble(val);
            } else if (targetClass == byte.class) {
                return Byte.parseByte(val);
            } else if (targetClass == char.class) {
                if (val.length() != 1) {
                    throw new ConvertionException("String '" + val + "' can't be converted to char");
                }

                return val.charAt(0);
            }
        } catch (NumberFormatException e) {
            throw new ConvertionException("String '" + val + "' can't be converted to " + targetClass.getName());
        }

        throw new ConvertionException("Target class is not primitive");
    }

}
