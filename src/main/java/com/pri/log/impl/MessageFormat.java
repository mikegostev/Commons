package com.pri.log.impl;

import com.pri.log.LogRecord;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageFormat {

    static long apStartTime = System.currentTimeMillis();
    private ArrayList<LRField> fm;
    static Pattern pat = Pattern.compile("%d\\{([^}]+)\\}|%[cdPCDFLmMnprt%]|%s\\{([0-9]+)\\}");
    static Pattern paramPat = Pattern.compile("\\{([0-9])\\}");


    public MessageFormat(String fmtStr, String defaultDateFormat) {
        setFormat(fmtStr, defaultDateFormat);
    }

    public void setFormat(String fmtStr, String defaultDateFormat) {
        fm = new ArrayList<LRField>();
        Matcher m = pat.matcher(fmtStr);

        int lastPos = 0;
        while (m.find()) {
            if (lastPos < m.start()) {
                fm.add(new StaticText(fmtStr.substring(lastPos, m.start())));
            }

            char ch = fmtStr.charAt(m.start() + 1);

            switch (ch) {
                case 't':
                    fm.add(new LRField() {
                        @SuppressWarnings("unused")
                        public void appendValue(StringBuffer sb, LogRecord lr) {
                            sb.append(Thread.currentThread().getName());
                        }
                    });
                    break;
                case 'r':
                    fm.add(new LRField() {
                        @SuppressWarnings("unused")
                        public void appendValue(StringBuffer sb, LogRecord lr) {
                            sb.append(System.currentTimeMillis() - apStartTime);
                        }
                    });
                    break;
                case 'p':
                    fm.add(new LFLevel());
                    break;
                case '%':
                    fm.add(new StaticText("%"));
                    break;
                case 'n':
                    fm.add(new StaticText("\n"));
                    break;
                case 'M':
                    fm.add(new LFMethod());
                    break;
                case 'm':
                    fm.add(new LFMessage());
                    break;
                case 'L':
                    fm.add(new LFLine());
                    break;
                case 'F':
                    fm.add(new LFFile());
                    break;
                case 'D':
                    fm.add(new LFLocalDate());
                    break;
                case 'd':
                    if (m.group(1) == null) {
                        try {
                            fm.add(new LFDateFormatted(defaultDateFormat));
                        } catch (Throwable t) {
                            fm.add(new LFLocalDate());
                        }
                    } else {
                        try {
                            fm.add(new LFDateFormatted(m.group(1)));
                        } catch (Throwable t) {
                            fm.add(new LFLocalDate());
                        }
                    }
                    break;
                case 'c':
                    fm.add(new LFLoggerName());
                    break;
                case 'C':
                    fm.add(new LFClassName());
                    break;
                case 'P':
                    fm.add(new LFFullClassName());
                    break;
                case 's':
                    fm.add(new Shift(Integer.parseInt(m.group(2))));
                    break;

            }

            lastPos = m.end();
        }

        if (lastPos < fmtStr.length()) {
            fm.add(new StaticText(fmtStr.substring(lastPos)));
        }
    }

    public void format(StringBuffer sb, LogRecord lr) {
        int n = fm.size();
        for (int i = 0; i < n; i++) {
            fm.get(i).appendValue(sb, lr);
        }

        if (lr.getThrown() != null) {
            sb.append('\n');
            reportThrowable(sb, lr.getThrown());
        }
    }

    static public void substituteParams(StringBuffer sb, String message, Object[] param) {
        Matcher m = paramPat.matcher(message);

        while (m.find()) {
            int ref = Integer.parseInt(m.group(1));

            if (param != null && ref >= 0 && ref < param.length) {
                m.appendReplacement(sb, param[ref].toString());
            } else {
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);

    }

    static public void reportThrowable(StringBuffer sb, Throwable th) {
        StackTraceElement[] tl = th.getStackTrace();

        String msg = th.getMessage();

        msg = msg == null ? "" : msg;

        sb.append("Exception: ").append(th.getClass().getName()).append(": \"").append(msg).append("\"");

        int i;
        for (i = 0; i < tl.length; i++) {
            sb.append("\n\tat ").append(tl[i].toString());
        }

        if (th.getCause() != null) {
            sb.append("\n Caused By ");
            reportThrowable(sb, th.getCause());
        }
    }

    interface LRField {

        void appendValue(StringBuffer sb, LogRecord lr);
    }

    static class LFLoggerName implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(lr.getLoggerName());
        }
    }

    static class LFClassName implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            String cn = lr.getClassName();
            int ind = cn.lastIndexOf('.');

            if (ind == -1) {
                sb.append(cn);
            }

            sb.append(cn.substring(ind + 1));
        }
    }

    static class LFFullClassName implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(lr.getClassName());
        }
    }

    static class LFMethod implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(lr.getMethod());
        }
    }

    static class LFMessage implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            substituteParams(sb, lr.getMessage(), lr.getParams());
        }
    }

    static class LFLevel implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(lr.getLevel().toString());
        }
    }

    static class LFLine implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(lr.getFileLine());
        }
    }

    static class LFFile implements LRField {

        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(lr.getFileName());
        }
    }

    static class LFLocalDate implements LRField {

        @SuppressWarnings("unused")
        public void appendValue(StringBuffer sb, LogRecord lr) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

            sb.append(df.format(new Date()));
        }
    }

    static class LFDateFormatted implements LRField {

        DateFormat df;

        LFDateFormatted(String fmt) {
            df = new SimpleDateFormat(fmt);
        }

        @SuppressWarnings("unused")
        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(df.format(new Date()));
        }
    }

    static class StaticText implements LRField {

        String v;

        StaticText(String s) {
            v = s;
        }

        @SuppressWarnings("unused")
        public void appendValue(StringBuffer sb, LogRecord lr) {
            sb.append(v);
        }
    }

    static class Shift implements LRField {

        int pos;

        Shift(int p) {
            pos = p;
        }

        @SuppressWarnings("unused")
        public void appendValue(StringBuffer sb, LogRecord lr) {
            int n = pos - sb.length();
            for (n--; n > 0; n--) {
                sb.append(' ');
            }
        }
    }

}

