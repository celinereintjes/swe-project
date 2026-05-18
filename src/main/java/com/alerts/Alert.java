package com.alerts;

/**
 * Alert is the public interface for alerts produced by the system. Implementations
 * may add additional metadata via decorators.
 */
public interface Alert {
    String getPatientId();

    String getCondition();

    long getTimestamp();
}
