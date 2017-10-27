package uk.ac.ebi.mg.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultExecutorService {

    private static ExecutorService instance;

    public static void init() {
        instance = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static ExecutorService getExecutorService() {
        return instance;
    }

    public static void setExecutorService(ExecutorService svc) {
        instance = svc;
    }

    public static void shutdown() {
        if (instance != null) {
            instance.shutdown();
        }
    }

}
