package uk.ac.ebi.mg.reflectcall;

public interface OutputFormatter
{
 String format( Object obj, Class<?> sourceClass ) throws FormatterException;
}
