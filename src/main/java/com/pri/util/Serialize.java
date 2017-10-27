package com.pri.util;

import com.pri.log.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialize {

    private Serialize() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static Object toObject(byte[] byts) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(byts);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    public static byte[] toByteArray(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            return baos.toByteArray();
        } catch (Throwable e) {
            Log.error("Serialization failed", e);
        }

        return null;
    }
}
