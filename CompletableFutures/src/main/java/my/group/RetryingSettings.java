package my.group;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Builder(toBuilder = true)
@Data
public class RetryingSettings {
    @Builder.Default
    private long delayMultiplier = 2;
    @Builder.Default
    private int retryCount = 2;
    @Builder.Default
    private Duration timeout = Duration.ofSeconds(5);
    @Builder.Default
    private Duration delayBetweenRetry = Duration.ofMillis(200);
}
