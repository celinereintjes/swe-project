package com.alerts;

import com.data_management.Patient;

/**
 * Strategy interface used by AlertGenerator to perform specific checks.
 */
public interface AlertStrategy {
    void checkAlert(Patient patient, AlertGenerator generator);
}
