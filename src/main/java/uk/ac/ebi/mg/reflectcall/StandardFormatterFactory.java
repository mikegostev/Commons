package uk.ac.ebi.mg.reflectcall;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import uk.ac.ebi.mg.reflectcall.formatter.ArrayFormatter;
import uk.ac.ebi.mg.reflectcall.formatter.CollectionFormatter;
import uk.ac.ebi.mg.reflectcall.formatter.ObjectFormatter;
import uk.ac.ebi.mg.reflectcall.formatter.PrimitiveTypeFormatter;

public class StandardFormatterFactory implements FormatterFactory {

    private static StandardFormatterFactory instance;
    private static Map<Class<?>, OutputFormatter> standardFmt = new HashMap<Class<?>, OutputFormatter>();

    public static StandardFormatterFactory getInstance() {
        if (instance == null) {
            instance = new StandardFormatterFactory();
        }

        return instance;
    }

    private Map<Class<?>, OutputFormatter> customFmt;

    public StandardFormatterFactory() {
    }

    public StandardFormatterFactory(Map<Class<?>, OutputFormatter> cMap) {
        customFmt = cMap;
    }

    @Override
    public OutputFormatter getFormatter(Class<?> cls) {
        if (cls == String.class) {
            return ObjectFormatter.getInstance();
        } else {
            OutputFormatter fmt = null;

            if (customFmt != null) {
                fmt = customFmt.get(cls);
            }

            if (fmt == null) {
                fmt = standardFmt.get(cls);
            }

            if (fmt == null && cls.isPrimitive()) {
                fmt = PrimitiveTypeFormatter.getInstance();
            }

            if (fmt == null && cls.isArray()) {
                fmt = new ArrayFormatter(this);
            }

            if (Collection.class.isAssignableFrom(cls)) {
                fmt = new CollectionFormatter(this);
            }

            if (fmt != null) {
                return fmt;
            }

            return ObjectFormatter.getInstance();
        }

    }

}
