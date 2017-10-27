package uk.ac.ebi.mg.reflectcall.formatter;

import java.lang.reflect.Method;
import uk.ac.ebi.mg.reflectcall.OutputFormatter;
import uk.ac.ebi.mg.reflectcall.exception.FormatterException;

public class PrimitiveTypeFormatter implements OutputFormatter {

    private static PrimitiveTypeFormatter instance;

    public static PrimitiveTypeFormatter getInstance() {
        if (instance == null) {
            instance = new PrimitiveTypeFormatter();
        }

        return instance;
    }

    @Override
    public String format(Object obj, Class<?> prClass) throws FormatterException {
        try {
            Method valueOfMeth = String.class.getMethod("valueOf", prClass);
            return (String) valueOfMeth.invoke(null, obj) + "\n";
        } catch (Exception e) {
            throw new FormatterException("Can't invoke String.valueOf method for arg: " + prClass.getName(), e);
        }
    }

}
