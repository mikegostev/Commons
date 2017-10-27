package uk.ac.ebi.mg.reflectcall;

import java.lang.reflect.Type;


public interface ConverterFactory {

    public static final String arrayBrackets = "[]";
    public static final String hashBrackets = "{}";
    public static final String bracketOverridePrefix = "=";

    public static final String fabricMethodName = "newInstance";

    String2ValueConverter getConverter(Type cls, String value);
}
