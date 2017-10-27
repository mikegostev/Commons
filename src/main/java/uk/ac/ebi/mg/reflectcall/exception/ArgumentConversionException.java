package uk.ac.ebi.mg.reflectcall.exception;

public class ArgumentConversionException extends Exception {

    private final int argnum;

    public ArgumentConversionException(String msg, int argn) {
        super(msg);
        argnum = argn;
    }

    public int getArgnum() {
        return argnum;
    }
}
