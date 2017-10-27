/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.pri.log.impl;

import com.pri.log.Level;
import com.pri.log.LogConfigurationException;
import com.pri.log.LogRecord;
import com.pri.log.Logger;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;


/**
 * <p>Simple implementation of Log that sends all enabled log messages, for all defined loggers, to System.err.  The
 * following system properties are supported to configure the behavior of this logger:</p> <ul>
 * <li><code>org.apache.commons.logging.simplelog.defaultlog</code> - Default logging detail level for all instances of
 * SimpleLog. Must be one of ("trace", "debug", "info", "warn", "error", or "fatal"). If not specified, defaults to
 * "info". </li> <li><code>org.apache.commons.logging.simplelog.log.xxxxx</code> - Logging detail level for a SimpleLog
 * instance named "xxxxx". Must be one of ("trace", "debug", "info", "warn", "error", or "fatal"). If not specified, the
 * default logging detail level is used.</li> <li><code>org.apache.commons.logging.simplelog.showlogname</code> - Set to
 * <code>true</code> if you want the Log instance name to be included in output messages. Defaults to
 * <code>false</code>.</li> <li><code>org.apache.commons.logging.simplelog.showShortLogname</code> - Set to
 * <code>true</code> if you want the last component of the name to be included in output messages. Defaults to
 * <code>true</code>.</li> <li><code>org.apache.commons.logging.simplelog.showdatetime</code> - Set to <code>true</code>
 * if you want the current date and time to be included in output messages. Default is <code>false</code>.</li>
 * <li><code>org.apache.commons.logging.simplelog.dateTimeFormat</code> - The date and time format to be used in the
 * output messages. The pattern describing the date and time format is the same that is used in
 * <code>java.text.SimpleDateFormat</code>. If the format is not specified or is invalid, the default format is used.
 * The default format is <code>yyyy/MM/dd HH:mm:ss:SSS zzz</code>.</li> </ul>
 *
 * <p>In addition to looking for system properties with the names specified above, this implementation also checks for a
 * class loader resource named <code>"simplelog.properties"</code>, and includes any matching definitions from this
 * resource (if it exists).</p>
 *
 * @author <a href="mailto:sanders@apache.org">Scott Sanders</a>
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 * @version $Id: SimpleLog.java,v 1.1 2007/08/06 23:41:03 mike Exp $
 */
public class SimpleLog extends Logger implements Serializable

{

    // ------------------------------------------------------- Class Attributes

    /** All system properties used by <code>SimpleLog</code> start with this */
    static protected final String systemPrefix = "com.pri.log.";
    static protected final String propertiesRes = "/META-INF/simplelog.properties";
    static protected final Properties simpleLogProps = new Properties();

    static protected final String PROP_LEVEL = "level";
    static protected final String PROP_SYSERR = "syserr";
    static protected final String PROP_DATEFORMAT = "dateFormat";
    static protected final String PROP_LOGFORMAT = "logFormat";

    /** The default format to use when formating dates */
    static protected final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";

    static protected final String DEFAULT_LOG_FORMAT = "[%p]%s{9}[%D] %C.%M(%F:%L) Th:%t%n%m";

    static protected String dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;

    static private long lastOutTime = 0;
    static private boolean lastWasErr = false;

    protected String logName = null;

    protected Level currentLogLevel;
    protected Level errLogLevel = Level.WARN;

    private MessageFormat messageFormat;

    private static String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            ; // Ignore
        }
        return (prop == null) ? simpleLogProps.getProperty(name) : prop;
    }

// private static String getStringProperty(String name, String dephault)
// {
//  String prop = getStringProperty(name);
//  return (prop == null) ? dephault : prop;
// }
//
// private static boolean getBooleanProperty(String name, boolean dephault)
// {
//  String prop = getStringProperty(name);
//  return (prop == null) ? dephault : "true".equalsIgnoreCase(prop);
// }

    // Initialize class attributes.
    // Load properties file, if found.
    // Override with system properties.
    static {
        // Add props from the resource simplelog.properties
        InputStream in = getResourceAsStream(propertiesRes);
        if (null != in) {
            try {
                simpleLogProps.load(in);
                in.close();
            } catch (java.io.IOException e) {
                // ignored
            }
        }

    }

    // ------------------------------------------------------------ Constructor

    /**
     * Construct a simple log with given name.
     *
     * @param name log name
     */
    protected String findProp(String type) {
        String name = logName;
        while (true) {
            String prop = getStringProperty(systemPrefix + type + "." + name);

            if (prop != null) {
                return prop;
            }

            int pos = name.lastIndexOf('.');

            if (pos == -1) {
                return getStringProperty(systemPrefix + type);
            }

            name = name.substring(0, pos);
        }
    }

    protected Level str2Level(String lvl) {
        if ("all".equalsIgnoreCase(lvl)) {
            return (Level.ALL);
        } else if ("trace".equalsIgnoreCase(lvl)) {
            return (Level.TRACE);
        } else if ("debug".equalsIgnoreCase(lvl)) {
            return (Level.DEBUG);
        } else if ("info".equalsIgnoreCase(lvl)) {
            return (Level.INFO);
        } else if ("warn".equalsIgnoreCase(lvl)) {
            return (Level.WARN);
        } else if ("error".equalsIgnoreCase(lvl)) {
            return (Level.ERROR);
        } else if ("fatal".equalsIgnoreCase(lvl)) {
            return (Level.FATAL);
        } else if ("off".equalsIgnoreCase(lvl)) {
            return (Level.OFF);
        }

        return null;
    }

    public SimpleLog(String name) {

        logName = name;

        // Set initial log level
        // Used to be: set default log level to ERROR
        // IMHO it should be lower, but at least info ( costin ).
        setLevel(Level.INFO);

        // Set log level from properties
        String lvl = findProp(PROP_LEVEL);

        if (lvl == null) {
            lvl = getStringProperty(systemPrefix + "defaultLevel");
        }

        Level level = str2Level(lvl);

        if (level != null) {
            setLevel(level);
        }

        lvl = findProp(PROP_SYSERR);
        if (lvl != null) {
            level = str2Level(lvl);

            if (level != null) {
                errLogLevel = level;
            }
        }

        lvl = findProp(PROP_DATEFORMAT);

        if (lvl != null) {
            dateTimeFormat = lvl;
        }

        lvl = findProp(PROP_LOGFORMAT);

        if (lvl == null) {
            messageFormat = new MessageFormat(DEFAULT_LOG_FORMAT, dateTimeFormat);
        } else {
            messageFormat = new MessageFormat(lvl, dateTimeFormat);
        }


    }

    // -------------------------------------------------------- Properties

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(Level currentLogLevel) {

        this.currentLogLevel = currentLogLevel;

    }

    /**
     * <p> Get logging level. </p>
     */
    public Level getLevel() {

        return currentLogLevel;
    }

    // -------------------------------------------------------- Logging Methods

    /**
     * <p> Do the actual logging. This method assembles the message and then calls <code>write()</code> to cause it to
     * be written.</p>
     *
     * @param type One of the LOG_LEVEL_XXX constants defining the log level
     * @param message The message itself (typically a String)
     * @param t The exception whose stack trace should be logged
     */
    protected void log(Level type, String message, Object[] param, Throwable t) {
        log(type, message, param, t, new Throwable().getStackTrace()[2]);
    }

    protected void log(Level type, String message, Object[] param, Throwable t, StackTraceElement stel) {

        LogRecord lr = new LogRecord();

        lr.setClassName(stel.getClassName());
        lr.setFileLine(stel.getLineNumber());
        lr.setFileName(stel.getFileName());
        lr.setLevel(type);
        lr.setLoggerName(logName);
        lr.setMessage(message);
        lr.setMethod(stel.getMethodName());
        lr.setParams(param);
        lr.setThrown(t);

        log(lr);
    }

    protected void log(LogRecord lr) {
        StringBuffer sb = new StringBuffer();
        messageFormat.format(sb, lr);
//  sb.append( '\n' );

        if (errLogLevel.compareTo(lr.getLevel()) >= 0) {
            writeerr(sb);
        } else {
            writeout(sb);
        }
    }


    /**
     * <p>Write the content of the message accumulated in the specified <code>StringBuffer</code> to the appropriate
     * output destination.  The default implementation writes to <code>System.err</code>.</p>
     *
     * @param buffer A <code>StringBuffer</code> containing the accumulated text to be logged
     */
    protected static synchronized void writeerr(StringBuffer buffer) {
        if (!lastWasErr && System.currentTimeMillis() - lastOutTime <= 2) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
        }

        System.err.println(buffer.toString());
        lastOutTime = System.currentTimeMillis();
        lastWasErr = true;
    }

    protected static synchronized void writeout(StringBuffer buffer) {
        if (lastWasErr && System.currentTimeMillis() - lastOutTime <= 2) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
        }

        System.out.println(buffer.toString());
        lastOutTime = System.currentTimeMillis();
        lastWasErr = false;
    }

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    protected boolean isLevelEnabled(Level logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return logLevel.compareTo(currentLogLevel) <= 0;
    }

    // -------------------------------------------------------- Log Implementation

    /**
     * <p> Are debug messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code> concatenation to be avoided when the message
     * will be ignored by the logger. </p>
     */
    public final boolean isDebugEnabled() {

        return isLevelEnabled(Level.DEBUG);
    }

    /**
     * <p> Are error messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code> concatenation to be avoided when the message
     * will be ignored by the logger. </p>
     */
    public final boolean isErrorEnabled() {

        return isLevelEnabled(Level.ERROR);
    }

    /**
     * <p> Are fatal messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code> concatenation to be avoided when the message
     * will be ignored by the logger. </p>
     */
    public final boolean isFatalEnabled() {

        return isLevelEnabled(Level.FATAL);
    }

    /**
     * <p> Are info messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code> concatenation to be avoided when the message
     * will be ignored by the logger. </p>
     */
    public final boolean isInfoEnabled() {

        return isLevelEnabled(Level.INFO);
    }

    /**
     * <p> Are trace messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code> concatenation to be avoided when the message
     * will be ignored by the logger. </p>
     */
    public final boolean isTraceEnabled() {

        return isLevelEnabled(Level.TRACE);
    }

    /**
     * <p> Are warn messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code> concatenation to be avoided when the message
     * will be ignored by the logger. </p>
     */
    public final boolean isWarnEnabled() {

        return isLevelEnabled(Level.WARN);
    }

    /**
     * Return the thread context class loader if available. Otherwise return null.
     *
     * The thread context class loader is available for JDK 1.2 or later, if certain security conditions are met.
     *
     * @throws LogConfigurationException if a suitable class loader cannot be identified.
     */
    private static ClassLoader getContextClassLoader() {
        ClassLoader classLoader = null;

        if (classLoader == null) {
            try {
                // Are we running on a JDK 1.2 or later system?
                Method method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);

                // Get the thread context class loader (if there is one)
                try {
                    classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
                } catch (IllegalAccessException e) {
                    ; // ignore
                } catch (InvocationTargetException e) {
                    /**
                     * InvocationTargetException is thrown by 'invoke' when
                     * the method being invoked (getContextClassLoader) throws
                     * an exception.
                     *
                     * getContextClassLoader() throws SecurityException when
                     * the context class loader isn't an ancestor of the
                     * calling class's class loader, or if security
                     * permissions are restricted.
                     *
                     * In the first case (not related), we want to ignore and
                     * keep going.  We cannot help but also ignore the second
                     * with the logic below, but other calls elsewhere (to
                     * obtain a class loader) will trigger this exception where
                     * we can make a distinction.
                     */
                    if (e.getTargetException() instanceof SecurityException) {
                        ; // ignore
                    } else {
                        // Capture 'e.getTargetException()' exception for details
                        // alternate: log 'e.getTargetException()', and pass back 'e'.
                        throw new LogConfigurationException("Unexpected InvocationTargetException",
                                e.getTargetException());
                    }
                }
            } catch (NoSuchMethodException e) {
                // Assume we are running on JDK 1.1
                ; // ignore
            }
        }

        if (classLoader == null) {
            classLoader = SimpleLog.class.getClassLoader();
        }

        // Return the selected class loader
        return classLoader;
    }

    private static InputStream getResourceAsStream(final String name) {
        return (InputStream) AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                ClassLoader threadCL = getContextClassLoader();

                if (threadCL != null) {
                    return threadCL.getResourceAsStream(name);
                } else {
                    return ClassLoader.getSystemResourceAsStream(name);
                }
            }
        });
    }

    @Override
    public void trace(String message, Object... param) {
        if (isLevelEnabled(Level.TRACE)) {
            log(Level.TRACE, message, param, null);
        }
    }

    @Override
    public void trace(String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.TRACE)) {
            log(Level.TRACE, message, param, t);
        }
    }

    @Override
    public void debug(String message, Object... param) {
        if (isLevelEnabled(Level.DEBUG)) {
            log(Level.DEBUG, message, param, null);
        }
    }

    @Override
    public void debug(String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.DEBUG)) {
            log(Level.DEBUG, message, param, t);
        }
    }

    @Override
    public void info(String message, Object... param) {
        if (isLevelEnabled(Level.INFO)) {
            log(Level.INFO, message, param, null);
        }
    }

    @Override
    public void info(String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.INFO)) {
            log(Level.INFO, message, param, t);
        }
    }

    @Override
    public void warn(String message, Object... param) {
        if (isLevelEnabled(Level.WARN)) {
            log(Level.WARN, message, param, null);
        }
    }

    @Override
    public void warn(String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.WARN)) {
            log(Level.WARN, message, param, t);
        }
    }

    @Override
    public void error(String message, Object... param) {
        if (isLevelEnabled(Level.ERROR)) {
            log(Level.ERROR, message, param, null);
        }
    }

    @Override
    public void error(String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.ERROR)) {
            log(Level.ERROR, message, param, t);
        }
    }

    @Override
    public void fatal(String message, Object... param) {
        if (isLevelEnabled(Level.FATAL)) {
            log(Level.FATAL, message, param, null);
        }
    }

    @Override
    public void fatal(String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.FATAL)) {
            log(Level.FATAL, message, param, t);
        }
    }

    @Override
    public void trace(StackTraceElement caller, String message, Object... param) {
        if (isLevelEnabled(Level.TRACE)) {
            log(Level.TRACE, message, param, null, caller);
        }
    }

    @Override
    public void trace(StackTraceElement caller, String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.TRACE)) {
            log(Level.TRACE, message, param, t, caller);
        }
    }

    @Override
    public void debug(StackTraceElement caller, String message, Object... param) {
        if (isLevelEnabled(Level.DEBUG)) {
            log(Level.DEBUG, message, param, null, caller);
        }
    }

    @Override
    public void debug(StackTraceElement caller, String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.DEBUG)) {
            log(Level.DEBUG, message, param, t, caller);
        }
    }

    @Override
    public void info(StackTraceElement caller, String message, Object... param) {
        if (isLevelEnabled(Level.INFO)) {
            log(Level.INFO, message, param, null, caller);
        }
    }

    @Override
    public void info(StackTraceElement caller, String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.INFO)) {
            log(Level.INFO, message, param, t, caller);
        }
    }

    @Override
    public void warn(StackTraceElement caller, String message, Object... param) {
        if (isLevelEnabled(Level.WARN)) {
            log(Level.WARN, message, param, null, caller);
        }
    }

    @Override
    public void warn(StackTraceElement caller, String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.WARN)) {
            log(Level.WARN, message, param, t, caller);
        }
    }

    @Override
    public void error(StackTraceElement caller, String message, Object... param) {
        if (isLevelEnabled(Level.ERROR)) {
            log(Level.ERROR, message, param, null, caller);
        }
    }

    @Override
    public void error(StackTraceElement caller, String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.ERROR)) {
            log(Level.ERROR, message, param, t, caller);
        }
    }

    @Override
    public void fatal(StackTraceElement caller, String message, Object... param) {
        if (isLevelEnabled(Level.FATAL)) {
            log(Level.FATAL, message, param, null, caller);
        }
    }

    @Override
    public void fatal(StackTraceElement caller, String message, Throwable t, Object... param) {
        if (isLevelEnabled(Level.FATAL)) {
            log(Level.FATAL, message, param, t, caller);
        }
    }

}


