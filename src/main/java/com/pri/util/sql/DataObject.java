package com.pri.util.sql;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public abstract class DataObject<T extends DataObject> implements Serializable {

    protected Map<String, Object> map = new TreeMap<String, Object>();

    public Object getFieldValue(Field key) {
        return map.get(key.getName());
    }

    public Object getFieldValue(String key) {
        return map.get(key);
    }

    public void setFieldValue(Field key, Object val) {
        map.put(key.getName(), val);
    }

    public boolean isSet(Field key) {
        return map.containsKey(key.getName());
    }

    public void clear() {
        map.clear();
    }

    abstract public Field getIDField();

    abstract public Field getField(String name);

    abstract public String getTypeName();

    abstract public Collection<Field> getFields();

    abstract public T newInstance();

}
