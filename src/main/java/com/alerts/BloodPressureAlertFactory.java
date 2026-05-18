package com.alerts;

/**
 * Factory for creating blood pressure related alerts.
 */
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BasicAlert(patientId, condition, timestamp);
    }
}
