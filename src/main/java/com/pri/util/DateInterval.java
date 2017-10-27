package com.pri.util;

import java.io.Serializable;
import java.util.Date;

public class DateInterval implements Serializable {

    private long begin;
    private long end;

    public DateInterval(Date b, Date e) {
        begin = b.getTime();
        end = e.getTime();
    }

    public DateInterval(long b, long e) {
        begin = b;
        end = e;
    }

    public long getBeginTimestamp() {
        return begin;
    }

    public Date getBeginDate() {
        return new Date(begin);
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin.getTime();
    }

    public long getEndTimestamp() {
        return end;
    }

    public Date getEndDate() {
        return new Date(end);
    }

    public void setEnd(long e) {
        this.end = e;
    }

    public void setEnd(Date e) {
        this.end = e.getTime();
    }

}
