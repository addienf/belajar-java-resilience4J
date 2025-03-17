package pzn.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class RetryTest {

    void callMe(){
        log.info("Try Call Me");
        throw  new IllegalArgumentException("Ups Error");
    }

    @Test
    void createNewRetry() {
        Retry retry = Retry.ofDefaults("pzn");

        Runnable runnable = Retry.decorateRunnable(retry, this::callMe);
    }

    private String hello(){
        log.info("Call Say Hello");
        throw  new IllegalArgumentException("Ups Error say Helloo");
    }

    @Test
    void creaeSupplier() {
        Retry retry = Retry.ofDefaults("pzn");
        Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
        String result = supplier.get();
    }
}
