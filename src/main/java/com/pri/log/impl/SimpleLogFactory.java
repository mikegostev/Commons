package com.pri.log.impl;

import com.pri.log.LogConfigurationException;
import com.pri.log.LogFactory;
import com.pri.log.Logger;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class SimpleLogFactory extends LogFactory {

    protected Map<String, WeakReference<Logger>> instances = new WeakHashMap<String, WeakReference<Logger>>();

    public SimpleLogFactory() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public Object getAttribute(@SuppressWarnings("unused") String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public Logger getInstance(Class clazz) throws LogConfigurationException {

        return (getInstance(clazz.getName()));

    }

    public Logger getInstance(String name) throws LogConfigurationException {
        WeakReference<Logger> wr = instances.get(name);

        Logger instance;
        if (wr != null && (instance = wr.get()) != null) {
            return instance;
        }

        instance = createLogger(name);
        instances.put(name, new WeakReference<Logger>(instance));

        return instance;
    }

    protected Logger createLogger(String name) {
        return new SimpleLog(name);
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAttribute(@SuppressWarnings("unused") String name) {
        // TODO Auto-generated method stub

    }

    @Override
    @SuppressWarnings("unused")
    public void setAttribute(String name, Object value) {
        // TODO Auto-generated method stub

    }

}
