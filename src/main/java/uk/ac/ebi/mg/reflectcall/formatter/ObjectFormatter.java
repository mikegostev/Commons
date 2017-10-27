package uk.ac.ebi.mg.reflectcall.formatter;

import uk.ac.ebi.mg.reflectcall.OutputFormatter;
import uk.ac.ebi.mg.reflectcall.exception.FormatterException;

public class ObjectFormatter implements OutputFormatter {

    private static ObjectFormatter instance;

    public static ObjectFormatter getInstance() {
        if (instance == null) {
            instance = new ObjectFormatter();
        }

        return instance;
    }

    @Override
    public String format(Object obj, Class<?> sourceClass) throws FormatterException {
        return obj.toString() + "\n";
    }

}
