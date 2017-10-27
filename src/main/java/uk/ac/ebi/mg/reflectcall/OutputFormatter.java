package uk.ac.ebi.mg.reflectcall;

import uk.ac.ebi.mg.reflectcall.exception.FormatterException;

public interface OutputFormatter {

    String format(Object obj, Class<?> sourceClass) throws FormatterException;
}
