package com.alerts;

/**
 * Simple handler interface for alerts so tests can capture triggered alerts.
 */
public interface AlertHandler {
    void handle(Alert alert);
}
