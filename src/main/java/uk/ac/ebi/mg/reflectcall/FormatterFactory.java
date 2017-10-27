package uk.ac.ebi.mg.reflectcall;


public interface FormatterFactory {

    OutputFormatter getFormatter(Class<?> cls);
}
