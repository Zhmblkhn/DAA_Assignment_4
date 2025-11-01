package util;

/**
 * Common metrics interface for algorithm instrumentation.
 * Tracks operation counters and time measurements.
 */
public interface Metrics {
    void startTimer();
    void stopTimer();

    void incrementCounter(String name);
    long getCounter(String name);

    long getElapsedTimeNanos();
    void reset();
}
