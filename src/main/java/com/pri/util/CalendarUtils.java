/*
 * Created on 01.04.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * @author Mike
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class CalendarUtils {

    private static final long milleniumTime;
    private static final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    static {
        Calendar cl = new GregorianCalendar(utcTimeZone);
        cl.set(2000, 1, 1, 0, 0, 0);
        milleniumTime = cl.getTimeInMillis();
    }

    /**
     *
     */
    private CalendarUtils() {
    }

    public static long epoch70ToEpoch2kMillis(long ms) {
        return ms - milleniumTime;
    }

    public static long epoch2KToEpoch70Millis(long ms) {
        return ms + milleniumTime;
    }

    public static int epoch70ToEpoch2kSec(int s) {
        return s - (int) (milleniumTime) / 1000;
    }

    public static int epoch70ToEpoch2kSec(long ms) {
        return (int) ((ms - milleniumTime) / 1000);
    }

    public static int epoch2KToEpoch70Sec(int s) {
        return s + (int) (milleniumTime) / 1000;
    }

    public static Date epoch2KToDateSec(int s) {
        return new Date(s * 1000 + milleniumTime);
    }

    public static Date epoch2KToDateMillis(int ms) {
        return new Date(ms + milleniumTime);
    }

    public static Calendar epoch2KToCalendarSec(int s) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(s * 1000 + milleniumTime);
        return cal;
    }

    public static Calendar epoch2KToCalendarMillis(int ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms + milleniumTime);
        return cal;
    }

    public static int calendarToEpoch2KSec(Calendar c) {
        return (int) ((c.getTimeInMillis() - milleniumTime) / 1000);
    }

    public static long dateToEpoch2KMillis(Date c) {
        return c.getTime() - milleniumTime;
    }

    public static int dateToEpoch2KSec(Date c) {
        return (int) ((c.getTime() - milleniumTime) / 1000);
    }

    public static long calendarToEpoch2KMillis(Calendar c) {
        return c.getTimeInMillis() - milleniumTime;
    }

    public static TimeZone getUTCTimeZone() {
        return utcTimeZone;
    }
}
