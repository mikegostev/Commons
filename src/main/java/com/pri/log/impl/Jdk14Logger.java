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


import com.pri.log.Logger;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * <p>Implementation of the <code>org.apache.commons.logging.Log</code> interface that wraps the standard JDK logging
 * mechanisms that were introduced in the Merlin release (JDK 1.4).</p>
 *
 * @author <a href="mailto:sanders@apache.org">Scott Sanders</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2007/08/06 23:41:03 $
 */

public class Jdk14Logger extends Logger implements Serializable

{

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a named instance of this Logger.
     *
     * @param name Name of the logger to be constructed
     */
    public Jdk14Logger(String name) {

        this.name = name;
        logger = getLogger();

    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The underling Logger implementation we are using.
     */
    protected transient java.util.logging.Logger logger = null;

    /**
     * The name of the logger we are wrapping.
     */
    protected String name = null;

    // --------------------------------------------------------- Public Methods

    private void log(Level level, String msg, Object[] param, Throwable ex, StackTraceElement caller) {
        try {
            java.util.logging.Logger logger = getLogger();
            if (logger.isLoggable(level)) {
                LogRecord lr = new LogRecord(level, msg);

                lr.setLoggerName(name);

                java.util.logging.Logger target = logger;
                while (target != null) {
                    String rbn = target.getResourceBundleName();
                    if (rbn != null) {
                        lr.setResourceBundleName(rbn);
                        lr.setResourceBundle(target.getResourceBundle());
                        break;
                    }
                    target = target.getParent();
                }

                Object[] params = null;

                if (param != null) {
                    params = new Object[param.length + 1];
                    for (int i = 0; i < param.length; i++) {
                        params[i] = param[i];
                    }
                } else {
                    params = new Object[1];
                }

                String cname = "unknown";
                String method = "unknown";

                if (caller == null) {
                    // Hack (?) to get the stack trace.
                    Throwable dummyException = new Throwable();
                    StackTraceElement locations[] = dummyException.getStackTrace();
                    // Caller will be the third element
                    if (locations != null && locations.length > 2) {
                        caller = locations[2];
                        cname = caller.getClassName();
                        method = caller.getMethodName();
                    }
                } else {
                    cname = caller.getClassName();
                    method = caller.getMethodName();
                }

                params[params.length - 1] = caller;

                lr.setSourceClassName(cname);
                lr.setSourceMethodName(method);

                if (ex != null) {
                    lr.setThrown(ex);
                }

                lr.setParameters(params);

                logger.log(lr);
            }
        } catch (Throwable t) {
            System.err.println("Exception while logging: " + t);
        }
    }

    /**
     * Return the native Logger instance we are using.
     */
    public java.util.logging.Logger getLogger() {
        if (logger == null) {
            logger = java.util.logging.Logger.getLogger(name);
        }
        return (logger);
    }

    /**
     * Is debug logging currently enabled?
     */
    public boolean isDebugEnabled() {
        return (getLogger().isLoggable(Level.FINE));
    }

    /**
     * Is error logging currently enabled?
     */
    public boolean isErrorEnabled() {
        return (getLogger().isLoggable(Level.SEVERE));
    }

    /**
     * Is fatal logging currently enabled?
     */
    public boolean isFatalEnabled() {
        return (getLogger().isLoggable(Level.SEVERE));
    }

    /**
     * Is info logging currently enabled?
     */
    public boolean isInfoEnabled() {
        return (getLogger().isLoggable(Level.INFO));
    }

    /**
     * Is trace logging currently enabled?
     */
    public boolean isTraceEnabled() {
        return (getLogger().isLoggable(Level.FINEST));
    }

    /**
     * Is warn logging currently enabled?
     */
    public boolean isWarnEnabled() {
        return (getLogger().isLoggable(Level.WARNING));
    }

    @Override
    public void trace(String message, Object... param) {
        log(Level.FINEST, message, param, null, null);
    }

    @Override
    public void trace(String message, Throwable t, Object... param) {
        log(Level.FINEST, message, param, t, null);
    }

    @Override
    public void debug(String message, Object... param) {
        log(Level.FINE, message, param, null, null);
    }

    @Override
    public void debug(String message, Throwable t, Object... param) {
        log(Level.FINE, message, param, t, null);
    }

    @Override
    public void info(String message, Object... param) {
        log(Level.INFO, message, param, null, null);
    }

    @Override
    public void info(String message, Throwable t, Object... param) {
        log(Level.INFO, message, param, t, null);
    }

    @Override
    public void warn(String message, Object... param) {
        log(Level.WARNING, message, param, null, null);
    }

    @Override
    public void warn(String message, Throwable t, Object... param) {
        log(Level.WARNING, message, param, t, null);
    }

    @Override
    public void error(String message, Object... param) {
        log(Level.SEVERE, message, param, null, null);
    }

    @Override
    public void error(String message, Throwable t, Object... param) {
        log(Level.SEVERE, message, param, t, null);
    }

    @Override
    public void fatal(String message, Object... param) {
        log(Level.SEVERE, message, param, null, null);
    }

    @Override
    public void fatal(String message, Throwable t, Object... param) {
        log(Level.SEVERE, message, param, t, null);
    }

    @Override
    public void trace(StackTraceElement caller, String message, Object... param) {
        log(Level.FINEST, message, param, null, caller);
    }

    @Override
    public void trace(StackTraceElement caller, String message, Throwable t, Object... param) {
        log(Level.FINEST, message, param, t, caller);
    }

    @Override
    public void debug(StackTraceElement caller, String message, Object... param) {
        log(Level.FINE, message, param, null, caller);
    }

    @Override
    public void debug(StackTraceElement caller, String message, Throwable t, Object... param) {
        log(Level.FINE, message, param, t, caller);
    }

    @Override
    public void info(StackTraceElement caller, String message, Object... param) {
        log(Level.INFO, message, param, null, caller);
    }

    @Override
    public void info(StackTraceElement caller, String message, Throwable t, Object... param) {
        log(Level.INFO, message, param, t, caller);
    }

    @Override
    public void warn(StackTraceElement caller, String message, Object... param) {
        log(Level.WARNING, message, param, null, caller);
    }

    @Override
    public void warn(StackTraceElement caller, String message, Throwable t, Object... param) {
        log(Level.WARNING, message, param, t, caller);
    }

    @Override
    public void error(StackTraceElement caller, String message, Object... param) {
        log(Level.SEVERE, message, param, null, caller);
    }

    @Override
    public void error(StackTraceElement caller, String message, Throwable t, Object... param) {
        log(Level.SEVERE, message, param, t, caller);
    }

    @Override
    public void fatal(StackTraceElement caller, String message, Object... param) {
        log(Level.SEVERE, message, param, null, caller);
    }

    @Override
    public void fatal(StackTraceElement caller, String message, Throwable t, Object... param) {
        log(Level.SEVERE, message, param, t, caller);
    }

    public String toString() {
        return name;
    }
}
