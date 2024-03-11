package my.group;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unchecked")
class CompletableFutureRetryingUtilsTest {
    private static final String ERROR_MESSAGE = "Custom error message";
    private static final String EXPECTED = "expected";

    @Test
    void invoke_retryingOfFailedExecutionIsExpected_shouldInvokeSupplierTwiceAndReturnCorrectResult() {
        final int retryCount = 3;

        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.completedFuture(EXPECTED);
        CompletableFuture<String> exceptionalCompletableFuture = new CompletableFuture<>();
        exceptionalCompletableFuture.completeExceptionally(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(exceptionalCompletableFuture).thenReturn(stringCompletableFuture);

        String result = CompletableFutureRetryingUtils.invoke(supplier, retryCount).join();

        assertEquals(EXPECTED, result);
        verify(supplier, times(2)).get();
    }

    @Test
    void invoke_notExpectedToBeRetried_shouldReturnException() {
        final int retryCount = 0;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> e = new CompletableFuture<>();
        e.completeExceptionally(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(e);
        var result = CompletableFutureRetryingUtils.invoke(supplier, retryCount);
        Assertions.assertThrows(RuntimeException.class, result::join);
    }

    @Test
    void invoke_noErrors_shouldInvokeSupplierOnlyOnce() {
        final int retryCount = 0;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.completedFuture(EXPECTED);
        when(supplier.get()).thenReturn(f);
        String result = CompletableFutureRetryingUtils.invoke(supplier, retryCount).join();
        assertEquals(EXPECTED, result);
        verify(supplier, times(retryCount + 1)).get();
    }

    @Test
    void invoke_errorsAreThrownOnEveryInvoke_shouldReturnExceptionAndInvokeSupplierTwice() {
        final int retryCount = 2;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> e = new CompletableFuture<>();
        e.completeExceptionally(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(e);
        try {
            CompletableFutureRetryingUtils.invoke(supplier, retryCount).join();
        } catch (CompletionException ex) {
            assertEquals(ERROR_MESSAGE, ex.getCause().getMessage());
        }
        verify(supplier, times(retryCount + 1)).get();
    }

    @Test
    void invokeAndRetry_firstAndSecondAreFailedButThirdsOk_ShouldInvokeSupplierTwice() {
        final int retryCount = 4;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.completedFuture(EXPECTED);
        CompletableFuture<String> e = new CompletableFuture<>();
        e.completeExceptionally(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(e).thenReturn(e).thenReturn(f);
        String result = CompletableFutureRetryingUtils.invoke(supplier, retryCount, 10, TimeUnit.MILLISECONDS).join();
        assertEquals(EXPECTED, result);
        verify(supplier, times(3)).get();
    }


    @Test
    void invokeAndDelay_retryCountIsOneAndAlwaysException_shouldReturnExceptionAndInvokeSupplierTwice() {
        final int retryCount = 2;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> e = new CompletableFuture<>();
        e.completeExceptionally(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(e);
        try {
            CompletableFutureRetryingUtils.invoke(supplier, retryCount, 10, TimeUnit.MILLISECONDS).join();
        } catch (CompletionException ex) {
            assertEquals(ERROR_MESSAGE, ex.getCause().getMessage());
        }
        verify(supplier, times(retryCount + 1)).get();
    }


    @Test
    void invokeAndDelay_retryCountIsZeroAndOk_ShouldInvokeSupplierOnlyOnce() {
        final int retryCount = 0;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.completedFuture(EXPECTED);
        when(supplier.get()).thenReturn(f);
        CompletableFutureRetryingUtils.invoke(supplier, retryCount, 10, TimeUnit.MILLISECONDS);
        verify(supplier, timeout(5).times(retryCount + 1)).get();
    }

    @Test
    void invokeAndDelay_retryCountIsOneAndOk_ShouldInvokeSupplierOnlyOnce() {
        final int retryCount = 1;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.completedFuture(EXPECTED);
        // First attempt is OK
        when(supplier.get()).thenReturn(f);
        CompletableFutureRetryingUtils.invoke(supplier, retryCount, 10, TimeUnit.MILLISECONDS);
        verify(supplier, timeout(5).times(1)).get();
    }

    @Test
    void invokeAndTimeout_retryCountIsOneAndOk_shouldInvokeSupplierOnlyOnce() {
        final int retryCount = 1;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.completedFuture(EXPECTED);
        when(supplier.get()).thenReturn(f);
        RetryingSettings retryAndTimeoutSettings = RetryingSettings.builder()
                .delayBetweenRetry(Duration.ofMillis(10))
                .delayMultiplier(1)
                .retryCount(retryCount)
                .timeout(Duration.ofMillis(10))
                .build();
        CompletableFutureRetryingUtils.invoke(supplier, retryAndTimeoutSettings, throwable -> true);
        verify(supplier, timeout(1000)).get();
    }

    @Test
    void invokeAndTimeout_retryCountIsOneAndError_ShouldInvokeSupplierTwice() {
        final int retryCount = 1;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.failedFuture(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(f);
        RetryingSettings retryAndTimeoutSettings = RetryingSettings.builder()
                .delayBetweenRetry(Duration.ofMillis(10))
                .delayMultiplier(1)
                .retryCount(retryCount)
                .timeout(Duration.ofMillis(10))
                .build();
        CompletableFutureRetryingUtils.invoke(supplier, retryAndTimeoutSettings, throwable -> true);

        verify(supplier, timeout(1000).times(retryCount + 1)).get();
    }

    @Test
    void invokeAndTimeout_retryPredictIsNotMatched_ShouldInvokeSupplierOnlyOnce() {
        final int retryCount = 10;
        Predicate<? super Throwable> retryPredicate = e -> !(e instanceof RuntimeException);
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.failedFuture(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(f);
        RetryingSettings settings = RetryingSettings.builder()
                .delayBetweenRetry(Duration.ofMillis(10))
                .delayMultiplier(2)
                .retryCount(retryCount)
                .timeout(Duration.ofMillis(10))
                .build();
        CompletableFutureRetryingUtils.invoke(supplier, settings, retryPredicate);
        verify(supplier, timeout(1000)).get();
    }

    @Test
    void invokeAndTimeout_timeoutException_ShouldInvokeSupplierExpectedTimes() {
        final int retryCount = 3;
        Predicate<? super Throwable> retryPredicate = e -> !(e instanceof RuntimeException);
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.failedFuture(new TimeoutException(ERROR_MESSAGE));

        when(supplier.get()).thenReturn(f);
        RetryingSettings settings = RetryingSettings.builder()
                .delayBetweenRetry(Duration.ofMillis(10))
                .delayMultiplier(2)
                .retryCount(retryCount)
                .timeout(Duration.ofMillis(30))
                .build();
        CompletableFutureRetryingUtils.invoke(supplier, settings, retryPredicate);
        verify(supplier, timeout(1000).times(retryCount + 1)).get();
    }

    @Test
    void invokeAndTimeout_retryPredictIsMatched_shouldInvokeSupplierExpectedTimes() {
        final int retryCount = 1;
        Predicate<? super Throwable> retryPredicate = e -> e instanceof RuntimeException;
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFuture.failedFuture(new RuntimeException(ERROR_MESSAGE));
        when(supplier.get()).thenReturn(f);
        RetryingSettings settings = RetryingSettings.builder()
                .delayBetweenRetry(Duration.ofMillis(10))
                .delayMultiplier(2)
                .retryCount(retryCount)
                .timeout(Duration.ofMillis(10))
                .build();
        CompletableFutureRetryingUtils.invoke(supplier, settings, retryPredicate);
        verify(supplier, timeout(1000).times(retryCount + 1)).get();
    }

    @Test
    void invokeAndTimeout_timeout_ShouldInvokeSupplierExpectedTimes() {
        final int retryCount = 2;
        Duration timeout = Duration.ofMillis(10);
        Supplier<CompletableFuture<String>> supplier = mock(Supplier.class);
        CompletableFuture<String> f = CompletableFutureRetryingUtils.delay(Duration.ofMillis(timeout.toMillis() * 100)).thenApply(o -> EXPECTED);
        when(supplier.get()).thenReturn(f);
        RetryingSettings settings = RetryingSettings.builder()
                .delayBetweenRetry(Duration.ofMillis(10))
                .delayMultiplier(2)
                .retryCount(retryCount)
                .timeout(Duration.ofMillis(10))
                .build();
        CompletableFutureRetryingUtils.invoke(supplier, settings, throwable -> false);
        verify(supplier, timeout(1000).times(retryCount + 1)).get();
    }
}