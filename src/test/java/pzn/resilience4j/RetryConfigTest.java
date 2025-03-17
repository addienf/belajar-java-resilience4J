package pzn.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RetryConfigTest {
    @Test
    void retryConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(IllegalArgumentException.class)
                .build();

        Retry retry = Retry.of("pzn", config);

        Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
        String result = supplier.get();
    }

    private String hello(){
        log.info("Call Say Hello");
        throw  new IllegalArgumentException("Ups Error say Helloo");
    }

    void callMe(){
        log.info("Try Call Me");
        throw  new IllegalArgumentException("Ups Error");
    }

    @Test
    void retryRegistry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();

        Retry retry1 = registry.retry("name");
        Retry retry2 = registry.retry("name");

        Assertions.assertSame(retry1, retry2);

        retry1.executeRunnable(this::callMe);
    }

    @Test
    void retryRegistryWithConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .build();

        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Retry retry = registry.retry("pzn", "config");
        retry.executeRunnable(this::callMe);
    }
}
