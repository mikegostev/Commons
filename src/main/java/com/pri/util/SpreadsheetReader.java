package com.pri.util;

import java.util.ArrayList;
import java.util.List;

public class SpreadsheetReader {

    String text;
    String columnSep = "\t";

    int cpos = 0;
    int lpos;
    int ln = 0;

    int textLen;

    public SpreadsheetReader(String text) {
        textLen = text.length();

        while (cpos < textLen) {
            if (text.charAt(cpos) == '\r') {
                cpos++;
            } else if (text.charAt(cpos) == '\n') {
                ln++;
                cpos++;
            } else {
                break;
            }
        }

        {  // looking for column separator
            int commaPos = text.indexOf(',', cpos);
            int tabPos = text.indexOf('\t', cpos);

            commaPos = commaPos == -1 ? Integer.MAX_VALUE : commaPos;
            tabPos = tabPos == -1 ? Integer.MAX_VALUE : tabPos;

            if (commaPos < tabPos) {
                columnSep = ",";
            }
        }

        this.text = text;
    }

    public int getLineNumber() {
        return ln;
    }

    public int getCurrentPosition() {
        return cpos;
    }

    public int getLineBeginPosition() {
        return lpos;
    }

    public List<String> readRow(List<String> accum) {
        String line = readLine();

        if (line == null) {
            return null;
        }

        if (accum == null) {
            accum = new ArrayList<String>(50);
        } else {
            accum.clear();
        }

        StringUtils.splitExcelString(line, columnSep, accum);

        return accum;
    }

    public String readLine() {
        if (cpos >= textLen) {
            return null;
        }

        lpos = cpos;

        ln++;

        int pos = text.indexOf('\n', cpos);

        String line = null;

        if (pos == -1) {
            line = text.substring(cpos);
            cpos = text.length();
        } else {
            int tpos = cpos;
            cpos = pos + 1;

            if (text.charAt(pos - 1) == '\r') {
                pos--;
            }

            line = text.substring(tpos, pos);
        }

        return line;
    }

}