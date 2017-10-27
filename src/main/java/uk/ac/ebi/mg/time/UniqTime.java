package uk.ac.ebi.mg.time;

public class UniqTime {

    private static long lastTime = 0;

    public static synchronized long getTime() {
        long cTime = System.currentTimeMillis();

        if (cTime <= lastTime) {
            cTime = ++lastTime;
        } else {
            lastTime = cTime;
        }

        return cTime;
    }
}
