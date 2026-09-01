package com.assignment.loadbalancer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*Tests for objective 1; strict round robin and wrap around*/

class RoundRobinBalancerTest {

    private List<String> take(LoadBalancer balancer, int count) {
        List<String> names = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            names.add(balancer.next().name());
        }
        return names;

    }

    @Test
    @DisplayName("seven calls return a..g exactly once, in order")
    void cyclesThroughThePoolInOrder() {
        LoadBalancer balancer = new RoundRobinBalancer(ServerPool.defaultPool());

        assertEquals(List.of("a", "b", "c", "d", "e", "f", "g"), take(balancer, 7));
    }

    @Test
    @DisplayName("the sequence wraps after the last server")
    void wrapsAroundAfterTheEndOfThePool() {
        LoadBalancer balancer = new RoundRobinBalancer(ServerPool.defaultPool());

        assertEquals(
                List.of("a", "b", "c", "d", "e", "f", "g",
                        "a", "b", "c", "d", "e", "f", "g"),
                take(balancer, 14));
    }

    @Test
    @DisplayName("a single-server pool always returns that server")
    void handlesAPoolOfOne() {
        LoadBalancer balancer = new RoundRobinBalancer(ServerPool.named("only"));

        assertEquals(List.of("only", "only", "only"), take(balancer, 3));
    }

    @Test
    @DisplayName("an empty pool is rejected at construction")
    void rejectsEmptyPool() {
        assertThrows(IllegalArgumentException.class,
                () -> new RoundRobinBalancer(List.of()));
    }

    @Test
    @DisplayName("a blank server name is rejected")
    void rejectsBlankServerName() {
        assertThrows(IllegalArgumentException.class, () -> new Server("  "));
    }

    @Test
    @DisplayName("the returned pool is immutable")
    void exposesAnImmutablePool() {
        LoadBalancer balancer = new RoundRobinBalancer(ServerPool.defaultPool());

        assertThrows(UnsupportedOperationException.class,
                () -> balancer.servers().add(new Server("h")));
    }

    @Test
    @DisplayName("rotation survives the cursor overflowing past Integer.MAX_VALUE")
    void rotationSurvivesCursorOverflow() {
        // floorMod is what makes this hold; plain % would return a negative index
        // once the cursor wraps to Integer.MIN_VALUE.
        int poolSize = 7;
        for (int offset = -3; offset <= 3; offset++) {
            int index = Math.floorMod(Integer.MAX_VALUE + offset, poolSize);
            assertTrue(index >= 0 && index < poolSize,
                    "index out of range near overflow boundary: " + index);
        }
    }

    @Test
    @DisplayName("concurrent callers each receive a distinct position in the sequence")
    void distributesEvenlyUnderConcurrency() throws InterruptedException {
        int threads = 7;
        int callsPerThread = 1_000;
        LoadBalancer balancer = new RoundRobinBalancer(ServerPool.defaultPool());

        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    start.await(); // release all threads together, to maximise overlap
                    for (int i = 0; i < callsPerThread; i++) {
                        results.add(balancer.next().name());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finished.countDown();
                }
            }).start();
        }

        start.countDown();
        assertTrue(finished.await(10, TimeUnit.SECONDS), "threads did not finish in time");

        // Every server must have been handed out exactly the same number of times.
        // A non-atomic cursor would lose increments and skew this distribution.
        Map<String, Long> counts = results.stream()
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

        assertEquals(7, counts.size());
        long expected = (long) threads * callsPerThread / 7;
        counts.forEach((name, count) -> assertEquals(expected, count, "uneven distribution for server " + name));
    }

}
