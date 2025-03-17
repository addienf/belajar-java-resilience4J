package pzn.resilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Slf4j
public class SemaphoreBulkheadTest {

    @SneakyThrows
    public void slow(){
        log.info("Slow");
        Thread.sleep(5000L);
    }

    @Test
    void testSemaphore() {
        Bulkhead bulkhead = Bulkhead.ofDefaults("pzn");

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, this::slow);
            new Thread(runnable).start();
        }
    }


    @Test
    void testThreadPool() {
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("pzn");
        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> runnable =
                    ThreadPoolBulkhead.decorateRunnable(bulkhead, this::slow);
            runnable.get();
        }
    }

    @Test
    void testSemaphoreConfi() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        Bulkhead bulkhead = Bulkhead.of("pzn", config);
        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, this::slow);
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolConfig() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(1)
                .build();

        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("pzn", config);
        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> runnable =
                    ThreadPoolBulkhead.decorateRunnable(bulkhead, this::slow);
            runnable.get();
        }
    }

    @Test
    void semaphoreRegistry() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Bulkhead bulkhead = registry.bulkhead("pzn", "config");
        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, this::slow);
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolConfigRegistry() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(1)
                .build();

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);
        ThreadPoolBulkhead bulkhead = registry.bulkhead("pzn", config);
        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> runnable =
                    ThreadPoolBulkhead.decorateRunnable(bulkhead, this::slow);
            runnable.get();
        }
    }
}
