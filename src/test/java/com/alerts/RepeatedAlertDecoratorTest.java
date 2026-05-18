package com.alerts;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RepeatedAlertDecoratorTest {

    @Test
    void repeatsAndStops() throws InterruptedException {
        BasicAlert base = new BasicAlert("1", "Test", System.currentTimeMillis());
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 100);

        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(3);

        repeated.startRepeating(a -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        boolean reached = latch.await(500, TimeUnit.MILLISECONDS);
        repeated.stopRepeating();

        assertTrue(reached, "Should have fired at least 3 times");
        assertTrue(counter.get() >= 3);
    }
}
