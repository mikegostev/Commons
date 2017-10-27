package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Type;
import uk.ac.ebi.mg.reflectcall.exception.ConvertionException;

public interface String2ValueConverter {

    Object convert(String val, Type targetClass) throws ConvertionException;
}
