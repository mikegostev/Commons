package uk.ac.ebi.mg.assertlog;

public class LogFactory {

    public static Log getLog(java.lang.Class clazz) {
        return new Log(org.apache.commons.logging.LogFactory.getLog(clazz));
    }

    public static Log getLog(java.lang.String name) {
        return new Log(org.apache.commons.logging.LogFactory.getLog(name));
    }
}
