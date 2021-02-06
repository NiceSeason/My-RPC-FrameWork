package io.niceseason.rpc.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * 创建 ThreadPool(线程池) 的工具类
 */
@Slf4j
public class ThreadPoolFactoryUtils {
    /**
     * 线程池参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final static Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtils() {
    }

    public static ExecutorService createDefaultThreadPoolIfAbsent(String threadNamePrefix) {
        return createDefaultThreadPoolIfAbsent(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPoolIfAbsent(String threadNamePrefix, Boolean daemon) {
        ExecutorService executorService = THREAD_POOLS.computeIfAbsent(threadNamePrefix, new Function<String, ExecutorService>() {
            @Override
            public ExecutorService apply(String s) {
                return createDefaultThreadPool(threadNamePrefix, daemon);
            }
        });
        if (executorService.isShutdown() || executorService.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            executorService = createDefaultThreadPool(threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, executorService);
        }
        return executorService;
    }


    private static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }


    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }


    public static void shutDownAllThreadPool() {
        log.info("即将关闭所有线程池...");
        THREAD_POOLS.entrySet().parallelStream().forEach((entry)->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("关闭线程池[{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                //主线程会处于一种等待的状态，等待线程池中所有的线程都运行完毕后才继续运行。
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }

}
