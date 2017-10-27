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


import com.pri.log.LogConfigurationException;
import com.pri.log.LogFactory;
import com.pri.log.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;


public class LogFactoryImpl extends LogFactory

{

    // ----------------------------------------------------------- Constructors

    /**
     * Public no-arguments constructor required by the lookup mechanism.
     */
    public LogFactoryImpl() {
        super();
    }

    // ----------------------------------------------------- Manifest Constants

    public static final String LOG_PROPERTY = "com.pri.log.Logger";

    private static final String LOG_INTERFACE = "com.pri.log.Logger";

    // ----------------------------------------------------- Instance Variables

    protected Map<String, Object> attributes = new HashMap<String, Object>();

    protected Map<String, Logger> instances = new HashMap<String, Logger>();

    private String logClassName;

    protected Constructor logConstructor = null;

    protected Class logConstructorSignature[] = {java.lang.String.class};

    protected Method logMethod = null;

    protected Class logMethodSignature[] = {LogFactory.class};

    // --------------------------------------------------------- Public Methods

    public Object getAttribute(String name) {

        return (attributes.get(name));

    }

    public String[] getAttributeNames() {

        String results[] = new String[attributes.size()];
        int i = 0;
        for (String nm : attributes.keySet()) {
            results[i++] = nm;
        }

        return (results);

    }

    public Logger getInstance(Class clazz) throws LogConfigurationException {

        return (getInstance(clazz.getName()));

    }

    public Logger getInstance(String name) throws LogConfigurationException {

        Logger instance = instances.get(name);
        if (instance == null) {
            instance = createLogger(name);
            instances.put(name, instance);
        }
        return (instance);

    }

    public void release() {

        instances.clear();
    }

    public void removeAttribute(String name) {

        attributes.remove(name);

    }

    public void setAttribute(String name, Object value) {

        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }

    }

    // ------------------------------------------------------ Protected Methods

    protected String getLogClassName() {

        // Return the previously identified class name (if any)
        if (logClassName != null) {
            return logClassName;
        }

        logClassName = (String) getAttribute(LOG_PROPERTY);

        if (logClassName == null) {
            try {
                logClassName = System.getProperty(LOG_PROPERTY);
            } catch (SecurityException e) {
                ;
            }
        }

        if ((logClassName == null) && isLog4JAvailable()) {
            logClassName = "com.pri.log.impl.Log4JLogger";
        }

        if ((logClassName == null) && isJdk14Available()) {
            logClassName = "com.pri.log.impl.Jdk14Logger";
        }

        if ((logClassName == null) && isJdk13LumberjackAvailable()) {
            logClassName = "com.pri.log.impl.Jdk13LumberjackLogger";
        }

        if (logClassName == null) {
            logClassName = "com.pri.log.impl.SimpleLog";
        }

        return (logClassName);

    }

    protected Constructor getLogConstructor() throws LogConfigurationException {

        // Return the previously identified Constructor (if any)
        if (logConstructor != null) {
            return logConstructor;
        }

        String logClassName = getLogClassName();

        // Attempt to load the Log implementation class
        Class<?> logClass = null;
        Class<?> logInterface = null;
        try {
            logInterface = this.getClass().getClassLoader().loadClass(LOG_INTERFACE);
            logClass = loadClass(logClassName);
            if (logClass == null) {
                throw new LogConfigurationException("No suitable Log implementation for " + logClassName);
            }
            if (!logInterface.isAssignableFrom(logClass)) {
                Class interfaces[] = logClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (LOG_INTERFACE.equals(interfaces[i].getName())) {
                        throw new LogConfigurationException(
                                "Invalid class loader hierarchy.  " + "You have more than one version of '"
                                        + LOG_INTERFACE + "' visible, which is " + "not allowed.");
                    }
                }
                throw new LogConfigurationException(
                        "Class " + logClassName + " does not implement '" + LOG_INTERFACE + "'.");
            }
        } catch (Throwable t) {
            throw new LogConfigurationException(t);
        }

        // Identify the <code>setLogFactory</code> method (if there is one)
        try {
            logMethod = logClass.getMethod("setLogFactory", logMethodSignature);
        } catch (Throwable t) {
            logMethod = null;
        }

        // Identify the corresponding constructor to be used
        try {
            logConstructor = logClass.getConstructor(logConstructorSignature);
            return (logConstructor);
        } catch (Throwable t) {
            throw new LogConfigurationException(
                    "No suitable Log constructor " + logConstructorSignature + " for " + logClassName, t);
        }
    }

    /**
     * MUST KEEP THIS METHOD PRIVATE.
     *
     * <p>Exposing this method outside of <code>org.apache.commons.logging.LogFactoryImpl</code> will create a security
     * violation: This method uses <code>AccessController.doPrivileged()</code>. </p>
     *
     * Load a class, try first the thread class loader, and if it fails use the loader that loaded this class.
     */
    private static Class loadClass(final String name) throws ClassNotFoundException {
        Object result = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                ClassLoader threadCL = getContextClassLoader();
                if (threadCL != null) {
                    try {
                        return threadCL.loadClass(name);
                    } catch (ClassNotFoundException ex) {
                        // ignore
                    }
                }
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    return e;
                }
            }
        });

        if (result instanceof Class) {
            return (Class) result;
        }

        throw (ClassNotFoundException) result;
    }

    /**
     * Is <em>JDK 1.3 with Lumberjack</em> logging available?
     */
    protected boolean isJdk13LumberjackAvailable() {

        try {
            loadClass("java.util.logging.Logger");
            loadClass("com.pri.log.impl.Jdk13LumberjackLogger");
            return (true);
        } catch (Throwable t) {
            return (false);
        }

    }

    /**
     * <p>Return <code>true</code> if <em>JDK 1.4 or later</em> logging is available.  Also checks that the
     * <code>Throwable</code> class supports <code>getStackTrace()</code>, which is required by Jdk14Logger.</p>
     */
    protected boolean isJdk14Available() {

        try {
            loadClass("java.util.logging.Logger");
            loadClass("com.pri.log.impl.Jdk14Logger");
//   Class throwable = loadClass("java.lang.Throwable");
//   if(throwable.getDeclaredMethod("getStackTrace", (Class[]) null) == null)
//   {
//    return (false);
//   }
            return (true);
        } catch (Throwable t) {
            t.printStackTrace();
            return (false);
        }
    }

    /**
     * Is a <em>Log4J</em> implementation available?
     */
    protected boolean isLog4JAvailable() {

        try {
            loadClass("org.apache.log4j.Logger");
            loadClass("com.pri.log.impl.Log4JLogger");
            return (true);
        } catch (Throwable t) {
            return (false);
        }
    }

    /**
     * Create and return a new {@link org.apache.commons.logging.Log} instance for the specified name.
     *
     * @param name Name of the new logger
     * @throws LogConfigurationException if a new instance cannot be created
     */
    protected Logger createLogger(String name) throws LogConfigurationException {

        Logger instance = null;
        try {
            Object params[] = new Object[1];
            params[0] = name;
            instance = (Logger) getLogConstructor().newInstance(params);
            if (logMethod != null) {
                params[0] = this;
                logMethod.invoke(instance, params);
            }
            return (instance);
        } catch (InvocationTargetException e) {
            Throwable c = e.getTargetException();
            if (c != null) {
                throw new LogConfigurationException(c);
            } else {
                throw new LogConfigurationException(e);
            }
        } catch (Throwable t) {
            throw new LogConfigurationException(t);
        }

    }

}
