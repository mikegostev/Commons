package uk.ac.ebi.mg.assertlog;

public class Log {

    private org.apache.commons.logging.Log cmLog;

    Log(org.apache.commons.logging.Log clog) {
        cmLog = clog;
    }

    public boolean debug(java.lang.Object message) {
        cmLog.debug(message);

        return true;
    }

    public boolean debug(java.lang.Object message, java.lang.Throwable t) {
        cmLog.debug(message, t);

        return true;
    }

    public boolean error(java.lang.Object message) {
        cmLog.error(message);

        return true;
    }

    public boolean error(java.lang.Object message, java.lang.Throwable t) {
        cmLog.error(message, t);

        return true;
    }

    public boolean fatal(java.lang.Object message) {
        cmLog.fatal(message);

        return true;
    }

    public boolean fatal(java.lang.Object message, java.lang.Throwable t) {
        cmLog.fatal(message, t);

        return true;
    }

    public boolean info(java.lang.Object message) {
        cmLog.info(message);

        return true;
    }

    public boolean info(java.lang.Object message, java.lang.Throwable t) {
        cmLog.info(message, t);

        return true;
    }

    public boolean isDebugEnabled() {
        return cmLog.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return cmLog.isDebugEnabled();
    }

    public boolean isFatalEnabled() {
        return cmLog.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return cmLog.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return cmLog.isDebugEnabled();
    }

    public boolean isWarnEnabled() {
        return cmLog.isDebugEnabled();
    }

    public boolean trace(java.lang.Object message) {
        cmLog.trace(message);

        return true;
    }

    public boolean trace(java.lang.Object message, java.lang.Throwable t) {
        cmLog.trace(message, t);

        return true;
    }

    public boolean warn(java.lang.Object message) {
        cmLog.warn(message);

        return true;
    }

    public boolean warn(java.lang.Object message, java.lang.Throwable t) {
        cmLog.warn(message, t);

        return true;
    }
}
