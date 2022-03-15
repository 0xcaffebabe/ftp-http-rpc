package wang.ismy.fttp.endpoint.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Title: ThreadPoolUtils
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2021年05月10日 10:08
 */
public class ThreadPoolUtils {

    public static final ThreadFactory LONG_LIFE_POOL_THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger atomicInteger = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "long-life-thread-pool-" + atomicInteger.incrementAndGet());
        }
    };

    public static final ThreadFactory SHORT_LIFE_POOL_THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger atomicInteger = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "short-life-thread-pool-" + atomicInteger.incrementAndGet());
        }
    };

    /**
     * 长时间运行线程池
     */
    public static final ExecutorService LONG_LIFE_POOL = new ThreadPoolExecutor(32,
            64,
            0,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(4096),
            LONG_LIFE_POOL_THREAD_FACTORY, new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 短时间运行线程池
     */
    public static final ExecutorService SHORT_LIFE_POOL = new ThreadPoolExecutor(32,
            64,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(4096),
            SHORT_LIFE_POOL_THREAD_FACTORY,
            new ThreadPoolExecutor.CallerRunsPolicy());
}
