package com.nurkiewicz.rxjava.demo;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nurkiewicz.rxjava.util.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main2(String[] args) {
        Thread thread = new Thread(() -> doSomeWork(), "MyWorker");
        thread.setDaemon(true);
        thread.start();
    }

    public static void main3(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Work work = new Work();
        Future<Object> f1 = executor.submit(work::perform);
        CompletableFuture<Object> f2 = CompletableFuture.supplyAsync(work::perform, executor);
//...
        executor.shutdownNow();
    }

    public static void main4(String[] args) throws InterruptedException {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("MyPool-%d")
                .setDaemon(true)
                .build();

        RejectedExecutionHandler rejectedHandler =
                (runnable, executor) -> log.warn("Rejected task {}", runnable);

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);

        MetricRegistry metricRegistry = new MetricRegistry();
        Slf4jReporter reporter = Slf4jReporter
                .forRegistry(metricRegistry)
                .outputTo(LoggerFactory.getLogger(Main.class))
                .build();
        reporter.start(1, TimeUnit.SECONDS);

        Gauge<Integer> queueLen = queue::size;
        metricRegistry.register("queue", queueLen);

        ExecutorService executor = new ThreadPoolExecutor(
                10,  //core size
                10,  //max size
                0L, TimeUnit.MILLISECONDS,  //keep alive
                queue,  //work queue
                threadFactory,
                rejectedHandler
        );


        IntStream.range(0, 1000).forEach(x -> executor.submit(() -> {
            Sleeper.sleep(Duration.ofSeconds(1));
        }));

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

    }

    public static void main5(String[] args) {
        LongAdder adder = new LongAdder();
        adder.increment();
        adder.add(42);
        adder.sum();
        adder.sumThenReset();
    }

    public static void main6(String[] args) {
        AtomicInteger atomic = new AtomicInteger();

        int cur;
        do {
            cur = atomic.get();
        } while (!atomic.compareAndSet(cur, cur * 2));

        atomic.updateAndGet(x -> x * 2);
    }

    public static void main(String[] args) throws InterruptedException {
//        main2(args);
//        main3(args);
//        main4(args);
        main5(args);
//        main6(args);
//        main7();
    }

    private static void main7() {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();

        wordCount.merge("Lorem", 1, (x, y) -> x + y);
        wordCount.merge("Ipusum", 1, (x, y) -> x + y);
        wordCount.merge("Lorem", 1, (x, y) -> x + y);
        wordCount.merge("Lorem", 1, (x, y) -> x + y);

        System.out.println(wordCount);
    }

    private static void doSomeWork() {

    }

}

class Worker implements Runnable {

    private final BlockingQueue<Work> queue;

    Worker(BlockingQueue<Work> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Work work = queue.take();
                work.perform();
            }
        } catch (InterruptedException e) {
            //interrupting Worker
        }
    }
}

class Work {
    Object perform() {
        return null;
    }
};