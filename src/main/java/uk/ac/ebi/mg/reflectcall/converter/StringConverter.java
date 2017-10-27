package uk.ac.ebi.mg.reflectcall.converter;

import java.lang.reflect.Type;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public class StringConverter implements String2ValueConverter {

    private static StringConverter instance;

    @Override
    public Object convert(String val, Type targetClass) throws ConvertionException {
        return val;
    }

    public static String2ValueConverter getInstance() {
        if (instance == null) {
            instance = new StringConverter();
        }

        return instance;
    }

}
