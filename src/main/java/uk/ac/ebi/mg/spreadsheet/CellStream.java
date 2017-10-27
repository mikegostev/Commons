package uk.ac.ebi.mg.spreadsheet;

import java.io.IOException;

public interface CellStream {

    void addCell(String cont) throws IOException;

    void addDateCell(long ts) throws IOException;

    void nextCell() throws IOException;

    void nextRow() throws IOException;

    void start() throws IOException;

    void finish() throws IOException;
}
