package com.alerts;

/**
 * Factory for creating ECG related alerts.
 */
public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BasicAlert(patientId, condition, timestamp);
    }
}
