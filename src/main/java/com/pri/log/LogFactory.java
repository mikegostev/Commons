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

package com.pri.log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class LogFactory {

    // ----------------------------------------------------- Manifest Constants

    public static final String FACTORY_PROPERTY = "com.pri.log.LogFactory";
    public static final String DOMAIN_PROPERTY = "com.pri.log.domain";
    public static final String FACTORY_DEFAULT = "com.pri.log.impl.LogFactoryImpl";
    public static final String FACTORY_PROPERTIES = "com.pri.log.properties";
    protected static final String SERVICE_ID = "META-INF/services/com.pri.log.LogFactory";

    // ----------------------------------------------------------- Constructors

    /**
     * Protected constructor that is not available for public use.
     */
    protected LogFactory() {
    }

    // --------------------------------------------------------- Public Methods

    public abstract Object getAttribute(String name);

    public abstract String[] getAttributeNames();

    public abstract Logger getInstance(Class clazz) throws LogConfigurationException;

    public abstract Logger getInstance(String name) throws LogConfigurationException;

    public abstract void release();

    public abstract void removeAttribute(String name);

    public abstract void setAttribute(String name, Object value);

    protected abstract Logger createLogger(String name);

    // ------------------------------------------------------- Static Variables

    /**
     * The previously constructed <code>LogFactory</code> instances, keyed by the <code>ClassLoader</code> with which it
     * was created.
     */
    protected static LogFactory singleLogFactory = null;
    protected static Map<ClassLoader, LogFactory> factories = new HashMap<ClassLoader, LogFactory>();

    // --------------------------------------------------------- Static Methods

    public static void setLogFactory(LogFactory slf) {
        singleLogFactory = slf;
    }

    public static LogFactory getFactory() throws LogConfigurationException {
        if (singleLogFactory != null) {
            return singleLogFactory;
        }

        // Identify the class loader we will be using
        ClassLoader contextClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return getContextClassLoader();
            }
        });

        // Return any previously registered factory for this class loader
        LogFactory factory = getCachedFactory(contextClassLoader);
        if (factory != null) {
            return factory;
        }

        // Load properties file.
        // Will be used one way or another in the end.

        Properties props = null;
        try {
            InputStream stream = getResourceAsStream(contextClassLoader, FACTORY_PROPERTIES);

            if (stream != null) {
                props = new Properties();
                props.load(stream);
                stream.close();
            }
        } catch (IOException e) {
        } catch (SecurityException e) {
        }

        try {
            String factoryClass = System.getProperty(FACTORY_PROPERTY);
            if (factoryClass != null) {
                factory = newFactory(factoryClass, contextClassLoader);
            }
        } catch (SecurityException e) {
            ; // ignore
        }

        // Second, try to find a service by using the JDK1.3 jar
        // discovery mechanism. This will allow users to plug a logger
        // by just placing it in the lib/ directory of the webapp ( or in
        // CLASSPATH or equivalent ). This is similar to the second
        // step, except that it uses the (standard?) jdk1.3 location in the jar.

        if (factory == null) {
            try {
                InputStream is = getResourceAsStream(contextClassLoader, SERVICE_ID);

                if (is != null) {
                    // This code is needed by EBCDIC and other strange systems.
                    // It's a fix for bugs reported in xerces
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (java.io.UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }

                    String factoryClassName = rd.readLine();
                    rd.close();

                    if (factoryClassName != null && !"".equals(factoryClassName)) {

                        factory = newFactory(factoryClassName, contextClassLoader);
                    }
                }
            } catch (Exception ex) {
                ;
            }
        }

        // Third try a properties file.
        // If the properties file exists, it'll be read and the properties
        // used. IMHO ( costin ) System property and JDK1.3 jar service
        // should be enough for detecting the class name. The properties
        // should be used to set the attributes ( which may be specific to
        // the webapp, even if a default logger is set at JVM level by a
        // system property )

        if (factory == null && props != null) {
            String factoryClass = props.getProperty(FACTORY_PROPERTY);
            if (factoryClass != null) {
                factory = newFactory(factoryClass, contextClassLoader);
            }
        }

        // Fourth, try the fallback implementation class

        if (factory == null) {
            factory = newFactory(FACTORY_DEFAULT, LogFactory.class.getClassLoader());
        }

        if (factory != null) {
            /**
             * Always cache using context class loader.
             */
            cacheFactory(contextClassLoader, factory);

            if (props != null) {
                Enumeration names = props.propertyNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    String value = props.getProperty(name);
                    factory.setAttribute(name, value);

                    if (DOMAIN_PROPERTY.equals(name)) {
                        if ("package".equalsIgnoreCase(value)) {
                            Log.setLoggerPerClass(false);
                        } else {
                            Log.setLoggerPerClass(true);
                        }
                    }
                }
            }
        }

        return factory;
    }

    public static Logger getLogger(Class clazz) throws LogConfigurationException {
        return (getFactory().getInstance(clazz));
    }

    public static Logger getLogger(String name) throws LogConfigurationException {
//  LogFactory fact = getFactory();
//  System.out.println( fact.getClass().getName() );
//  return (fact.getInstance(name));

        return (getFactory().getInstance(name));
    }

    public static void release(ClassLoader classLoader) {
        synchronized (factories) {
            LogFactory factory = factories.get(classLoader);
            if (factory != null) {
                factory.release();
                factories.remove(classLoader);
            }
        }
    }

    public static void releaseAll() {
        if (singleLogFactory != null) {
            singleLogFactory.release();
            singleLogFactory = null;
        }

        synchronized (factories) {
            for (LogFactory fact : factories.values()) {
                fact.release();
            }
            factories.clear();
        }

    }

    // ------------------------------------------------------ Protected Methods

    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            return null;
        }
    }

    /**
     * Check cached factories (keyed by contextClassLoader)
     */
    private static LogFactory getCachedFactory(ClassLoader contextClassLoader) {
        LogFactory factory = null;

        if (contextClassLoader != null) {
            factory = factories.get(contextClassLoader);
        }

        return factory;
    }

    private static void cacheFactory(ClassLoader classLoader, LogFactory factory) {
        if (classLoader != null && factory != null) {
            factories.put(classLoader, factory);
        }
    }

    /**
     * Return a new instance of the specified <code>LogFactory</code> implementation class, loaded by the specified
     * class loader. If that fails, try the class loader used to load this (abstract) LogFactory.
     *
     * @param factoryClass Fully qualified name of the <code>LogFactory</code> implementation class
     * @param classLoader ClassLoader from which to load this class
     * @throws LogConfigurationException if a suitable instance cannot be created
     */
    protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader)
            throws LogConfigurationException {
        Object result = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                // This will be used to diagnose bad configurations
                // and allow a useful message to be sent to the user
                Class logFactoryClass = null;
                try {
                    if (classLoader != null) {
                        try {
                            // First the given class loader param (thread class loader)

                            // Warning: must typecast here & allow exception
                            // to be generated/caught & recast properly.
                            logFactoryClass = classLoader.loadClass(factoryClass);
                            return (LogFactory) logFactoryClass.newInstance();

                        } catch (ClassNotFoundException ex) {
                            if (classLoader == LogFactory.class.getClassLoader()) {
                                // Nothing more to try, onwards.
                                throw ex;
                            }
                            // ignore exception, continue
                        } catch (NoClassDefFoundError e) {
                            if (classLoader == LogFactory.class.getClassLoader()) {
                                // Nothing more to try, onwards.
                                throw e;
                            }

                        } catch (ClassCastException e) {

                            if (classLoader == LogFactory.class.getClassLoader()) {
                                // Nothing more to try, onwards (bug in loader implementation).
                                throw e;
                            }
                        }
                        // Ignore exception, continue
                    }

     /* At this point, either classLoader == null, OR
      * classLoader was unable to load factoryClass.
      * Try the class loader that loaded this class:
      * LogFactory.getClassLoader().
      *
      * Notes:
      * a) LogFactory.class.getClassLoader() may return 'null'
      *    if LogFactory is loaded by the bootstrap classloader.
      * b) The Java endorsed library mechanism is instead
      *    Class.forName(factoryClass);
      */
                    // Warning: must typecast here & allow exception
                    // to be generated/caught & recast properly.
                    logFactoryClass = Class.forName(factoryClass);
                    return (LogFactory) logFactoryClass.newInstance();
                } catch (Exception e) {
                    // Check to see if we've got a bad configuration
                    if (logFactoryClass != null && !LogFactory.class.isAssignableFrom(logFactoryClass)) {
                        return new LogConfigurationException(
                                "The chosen LogFactory implementation does not extend LogFactory."
                                        + " Please check your configuration.", e);
                    }
                    return new LogConfigurationException(e);
                }
            }
        });

        if (result instanceof LogConfigurationException) {
            throw (LogConfigurationException) result;
        }

        return (LogFactory) result;
    }

    private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
                if (loader != null) {
                    return loader.getResourceAsStream(name);
                }

                return ClassLoader.getSystemResourceAsStream(name);
            }
        });
    }
}
