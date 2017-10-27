package uk.ac.ebi.mg.reflectcall.formatter;

import java.lang.reflect.Array;
import uk.ac.ebi.mg.reflectcall.FormatterFactory;
import uk.ac.ebi.mg.reflectcall.OutputFormatter;
import uk.ac.ebi.mg.reflectcall.exception.FormatterException;

public class ArrayFormatter implements OutputFormatter {

    public static final String arraySeparatorString = "------------\n";

    private final FormatterFactory factory;

    public ArrayFormatter(FormatterFactory f) {
        factory = f;
    }

    @Override
    public String format(Object obj, Class<?> sourceClass) throws FormatterException {

        Class<?> elClass = sourceClass.getComponentType();
        OutputFormatter elFmt = factory.getFormatter(elClass);

        int len = Array.getLength(obj);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(arraySeparatorString);
            }

            sb.append(elFmt.format(Array.get(obj, i), elClass));
        }

        return sb.toString();
    }

}
