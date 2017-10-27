package uk.ac.ebi.mg.reflectcall.exception;

public class ConvertionException extends Exception {

    public ConvertionException() {
    }

    public ConvertionException(String string) {
        super(string);
    }

    public ConvertionException(String string, Exception e) {
        super(string, e);
    }

}
