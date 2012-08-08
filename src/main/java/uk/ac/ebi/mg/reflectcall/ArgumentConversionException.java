package uk.ac.ebi.mg.reflectcall;

public class ArgumentConversionException extends Exception
{
 private final int argnum; 
 
 public ArgumentConversionException(String msg, int argn)
 {
  super( msg );
  argnum = argn;
 }

 public int getArgnum()
 {
  return argnum;
 }
}
