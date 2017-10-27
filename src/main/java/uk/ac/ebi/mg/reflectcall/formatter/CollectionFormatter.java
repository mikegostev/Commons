package uk.ac.ebi.mg.reflectcall.formatter;

import java.util.Collection;
import uk.ac.ebi.mg.reflectcall.FormatterFactory;
import uk.ac.ebi.mg.reflectcall.OutputFormatter;
import uk.ac.ebi.mg.reflectcall.exception.FormatterException;

public class CollectionFormatter implements OutputFormatter {

    public static final String collectionSeparatorString = "------------\n";

    private final FormatterFactory factory;

    public CollectionFormatter(FormatterFactory f) {
        factory = f;
    }

    @Override
    public String format(Object obj, Class<?> sourceClass) throws FormatterException {

        Collection<?> coll = (Collection<?>) obj;

        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (Object el : coll) {
            OutputFormatter elFmt = factory.getFormatter(el.getClass());

            if (i++ > 0) {
                sb.append(collectionSeparatorString);
            }

            sb.append(elFmt.format(el, el.getClass()));
        }

        return sb.toString();
    }

}
