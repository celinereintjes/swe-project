package com.alerts;

/**
 * Factory for creating blood oxygen / saturation related alerts.
 */
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BasicAlert(patientId, condition, timestamp);
    }
}
