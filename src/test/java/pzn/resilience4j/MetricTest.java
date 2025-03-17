package pzn.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class MetricTest {

    String hello(){
        throw  new IllegalArgumentException("Ups");
    }

    @Test
    void retry() {
        Retry retry = Retry.ofDefaults("pzn");

        try {
            Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
            String result = supplier.get();
        } catch (Exception e){
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        }
    }

    @Test
    void eventPublisher() {
        Retry retry = Retry.ofDefaults("pzn");
        retry.getEventPublisher().onRetry(event -> log.info("Try to Retry"));

        try {
            Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
            String result = supplier.get();
        } catch (Exception e){
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        }
    }

    @Test
    void retryRegistry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.getEventPublisher().onEntryAdded(event -> log.info("New Entry Added"));

        Retry retry1 = registry.retry("name");
        Retry retry2 = registry.retry("name");
    }
}
