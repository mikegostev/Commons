package uk.ac.ebi.mg.reflectcall.converter;

import com.pri.util.StringUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.mg.reflectcall.ConverterFactory;
import uk.ac.ebi.mg.reflectcall.String2ValueConverter;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public class ArrayConverter implements String2ValueConverter {

    private final ConverterFactory convFactory;

    public ArrayConverter(ConverterFactory convFact) {
        convFactory = convFact;
    }


    @Override
    public Object convert(String val, Type targetType) throws ConvertionException {
        Class<?> elClass = ((Class<?>) targetType).getComponentType();

        List<String> parts = splitString(val);

        Object outarr = Array.newInstance(elClass, parts.size());

        int i = 0;
        for (String s : parts) {
            Array.set(outarr, i++, getFactory().getConverter(elClass, s).convert(s, elClass));
        }

        return outarr;
    }

    public ConverterFactory getFactory() {
        return convFactory;
    }

    protected static List<String> splitString(String val) {
        if (val.startsWith(ConverterFactory.bracketOverridePrefix)) {
            ArrayList<String> res = new ArrayList<String>(1);
            res.add(val.substring(ConverterFactory.bracketOverridePrefix.length()));
            return res;
        }

        if (val.charAt(val.length() - 1) == ConverterFactory.arrayBrackets.charAt(1) && val.length() > 2
                && val.charAt(0) == ConverterFactory.arrayBrackets.charAt(0)) {
            char sep = val.charAt(1);

            return StringUtils.splitString(val.substring(2, val.length() - 1), sep);
        }

        ArrayList<String> res = new ArrayList<String>(1);
        res.add(val);

        return res;
    }
}
