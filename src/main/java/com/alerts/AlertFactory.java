package com.alerts;

/**
 * Abstract factory for creating Alert instances.
 */
public abstract class AlertFactory {
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
