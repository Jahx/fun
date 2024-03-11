package my.group;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.function.Supplier;

@UtilityClass
public class CompletableFutureRetryingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletableFutureRetryingUtils.class);

    public static final String ERROR_ON_ATTEMPT_MESSAGE_TEMPLATE = "Error on attempt #{}, ";
    public static final String ERROR_ON_ATTEMPT_WITH_TIMEOUT_MESSAGE_TEMPLATE = "Error on attempt #{} - will be retried in {}ms.";
    public static final String RETRIES_EXHAUSTED_MESSAGE_TEMPLATE = "Retries exhausted ({})";

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(ONE);

    public static <T> CompletableFuture<T> invoke(Supplier<CompletableFuture<T>> supplier, int retryCount) {
        return invoke(supplier, retryCount, ZERO, null);
    }

    public static <T> CompletableFuture<T> invoke(Supplier<CompletableFuture<T>> supplier, int retryCount, int delay, TimeUnit unit) {
        return invoke(supplier, retryCount, delay, ONE, unit);
    }

    public static <T> CompletableFuture<T> invoke(Supplier<CompletableFuture<T>> supplier, int retryCount, int delay, int delayMultiplier, TimeUnit unit) {

        if (retryCount > ZERO) {
            CompletableFuture<InvokingResult<T>> resultCompletableFuture = supplier.get()
                    .thenApply(x -> new InvokingResult<>(x, false))
                    .exceptionally(e -> {
                        LOGGER.error(ERROR_ON_ATTEMPT_MESSAGE_TEMPLATE, ONE, e);
                        return new InvokingResult<>(null, true);
                    });

            return resultCompletableFuture
                    .thenCompose(invokingResult -> {
                        if (invokingResult.isExceptionally()) {
                            return invoke(supplier, retryCount, TWO, delay, delayMultiplier, unit);
                        } else {
                            return CompletableFuture.completedFuture(invokingResult.getValue());
                        }
                    });
        } else {
            return supplier.get();
        }
    }

    public static <T> CompletableFuture<T> invoke(Supplier<CompletableFuture<T>> supplier, RetryingSettings settings, Predicate<? super Throwable> retryPredicate) {
        return invoke(supplier, ONE, settings, retryPredicate);
    }

    public static <T> CompletableFuture<T> delay(int delay, TimeUnit unit) {
        if (delay > 0) {
            return delay(Duration.ofMillis(unit.toMillis(delay)));
        }
        return CompletableFuture.completedFuture(null);
    }

    public static <T> CompletableFuture<T> delay(Duration delay) {
        CompletableFuture<T> future = new CompletableFuture<>();
        ScheduledFuture<Boolean> task = scheduler.schedule(() ->
                future.complete(null), delay.toMillis(), TimeUnit.MILLISECONDS);
        return future.whenComplete((t, ex) -> {
            if (future.isCancelled()) {
                task.cancel(true);
            }
        });
    }

    private static <T> CompletableFuture<T> invoke(Supplier<CompletableFuture<T>> supplier, int attempt,
                                                   RetryingSettings settings, Predicate<? super Throwable> retryPredicate) {

        CompletableFuture<InvokingResult<T>> resultCompletableFuture = supplier.get()
                .orTimeout(settings.getTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(x -> new InvokingResult<>(x, false, null))
                .handle((result, throwable) -> {
                    if (throwable != null) {
                        return new InvokingResult<>(null, true, throwable);
                    }
                    return result;
                });

        return resultCompletableFuture.thenCompose(result -> {
            if (result.isExceptionally()) {
                Throwable throwable = result.getThrowable();
                if (attempt > settings.getRetryCount()) {
                    LOGGER.error(RETRIES_EXHAUSTED_MESSAGE_TEMPLATE, settings.getRetryCount());
                    return CompletableFuture.failedFuture(throwable);
                } else if (isTimeoutException(throwable) || retryPredicate.test(throwable)) {
                    Duration delay = settings.getDelayBetweenRetry();
                    LOGGER.warn(ERROR_ON_ATTEMPT_WITH_TIMEOUT_MESSAGE_TEMPLATE, attempt, delay.toMillis(), throwable);
                    RetryingSettings updatedSettings = settings.toBuilder()
                            .delayBetweenRetry(
                                    Duration.ofMillis(delay.toMillis() * settings.getDelayMultiplier())
                            ).build();
                    return delay(delay).thenCompose(x ->
                            invoke(supplier, attempt + ONE, updatedSettings, retryPredicate));
                } else {
                    return CompletableFuture.failedFuture(throwable);
                }
            } else {
                return CompletableFuture.completedFuture(result.getValue());
            }
        });

    }

    private static boolean isTimeoutException(Throwable throwable) {
        return throwable instanceof TimeoutException || Optional.ofNullable(throwable.getCause()).map(TimeoutException.class::isInstance).orElse(false);
    }

    private static <T> CompletableFuture<T> invoke(Supplier<CompletableFuture<T>> supplier, int maxRetryCount, int attempt, int delay, int delayMultiplier, TimeUnit unit) {
        return delay(delay, unit).thenCompose(ignore -> {
            if (attempt <= maxRetryCount) {
                return getResult(attempt, supplier.get()).thenCompose(result -> {
                    if (result.isExceptionally()) {
                        return invoke(supplier, maxRetryCount, attempt + ONE, delay * delayMultiplier, delayMultiplier, unit);
                    } else {
                        return CompletableFuture.completedFuture(result.getValue());
                    }
                });
            } else {
                return processErrorResult(supplier, attempt);
            }
        });
    }

    private static <T> CompletableFuture<InvokingResult<T>> getResult(int attempt, CompletableFuture<T> future) {
        return future
                .thenApply(x -> new InvokingResult<>(x, false)).exceptionally(throwable -> {
                    LOGGER.warn(ERROR_ON_ATTEMPT_MESSAGE_TEMPLATE, attempt, throwable);
                    return new InvokingResult<>(null, true);
                });
    }

    private static <T> CompletableFuture<T> processErrorResult(Supplier<CompletableFuture<T>> supplier, int attempt) {
        return supplier.get().exceptionally(throwable -> {
            LOGGER.error(ERROR_ON_ATTEMPT_MESSAGE_TEMPLATE, attempt, throwable);
            throw new CompletionException(throwable);
        });
    }
}

@Getter
class InvokingResult<T> {
    private final T value;
    private final boolean isExceptionally;
    private final Throwable throwable;

    InvokingResult(T value, boolean isExceptionally) {
        this.value = value;
        this.isExceptionally = isExceptionally;
        this.throwable = null;
    }

    InvokingResult(T value, boolean isExceptionally, Throwable exception) {
        this.value = value;
        this.isExceptionally = isExceptionally;
        this.throwable = exception;
    }
}

