package uk.ac.ebi.mg.reflectcall;


public interface ConverterFactory
{
 public static final String arrayBrackets = "[]";
 public static final String hashBrackets = "{}";
 public static final String bracketOverridePrefix = "=";

 public static final String fabricMethodName = "newInstance";
 
 String2ValueConverter getConverter( Class<?> cls, String value );
}
