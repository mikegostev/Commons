package uk.ac.ebi.mg.reflectcall;

public class ConvertionException extends Exception
{

 public ConvertionException()
 {
 }
 
 public ConvertionException(String string)
 {
  super( string );
 }

 public ConvertionException(String string, ReflectiveOperationException e)
 {
  super( string, e);
 }

}
