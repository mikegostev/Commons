package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Type;

public interface String2ValueConverter
{
 Object convert( String val, Type targetClass ) throws ConvertionException;
}
