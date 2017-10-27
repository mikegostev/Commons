package uk.ac.ebi.mg.spreadsheet;

import java.util.List;

public interface SpreadsheetReader {

    public static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public abstract int getLineNumber();

    public abstract List<String> readRow(List<String> accum);

}