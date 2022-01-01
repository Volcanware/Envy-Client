package mathax.client.utils.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatHaxExecutor {
    public static ExecutorService executor;

    public static void init() {
        executor = Executors.newSingleThreadExecutor();
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }
}
