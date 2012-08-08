package uk.ac.ebi.mg.reflectcall;

public interface String2ValueConverter
{
 Object convert( String val, Class<?> targetClass ) throws ConvertionException;
}
