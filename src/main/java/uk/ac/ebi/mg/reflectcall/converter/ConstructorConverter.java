package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public class ConstructorConverter implements String2ValueConverter {

    private final Constructor<?> constructor;

    public ConstructorConverter(Constructor<?> ctor) {
        constructor = ctor;
    }

    @Override
    public Object convert(String val, Type targetType) throws ConvertionException {
        try {
            return constructor.newInstance(val);
        } catch (Exception e) {
            throw new ConvertionException("Constructor call error: " + e.getMessage() + " Target class: " + targetType);
        }
    }

}
