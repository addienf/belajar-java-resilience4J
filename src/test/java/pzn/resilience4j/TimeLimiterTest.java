package pzn.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TimeLimiterTest {

    @SneakyThrows
    public long slow(){
        log.info("Start Slow");
        Thread.sleep(5000L);
        log.info("End Slow");
        return 10_000L;
    }

    @Test
    void testTimeLimiter() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Long> future = executorService.submit(this::slow);

        TimeLimiter limiter = TimeLimiter.ofDefaults("pzn");
        Callable<Long> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

    @Test
    void timeLimiterConfig() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Future<Long> future = executorService.submit(this::slow);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(20))
                .cancelRunningFuture(true)
                .build();

        TimeLimiter limiter = TimeLimiter.of("pzn", config);
        Callable<Long> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

    @Test
    void timeLimiterRegistry() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Future<Long> future = executorService.submit(this::slow);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(20))
                .cancelRunningFuture(true)
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        TimeLimiter limiter = registry.timeLimiter("pzn", "config");
        Callable<Long> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }
}
