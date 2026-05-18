package com.alerts;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;

class AlertGeneratorTest {

    @Test
    void lowSaturationTriggersAlert() {
    DataStorage storage = DataStorage.getInstance();
    storage.clear();
        long now = System.currentTimeMillis();
        // Add a saturation reading below threshold for patient 1
        storage.addPatientData(1, 90.0, "Saturation", now);

        AtomicBoolean alerted = new AtomicBoolean(false);
        AlertHandler handler = alert -> {
            String cond = alert.getCondition().toLowerCase();
            if (cond.contains("saturation") || cond.contains("spo2") || cond.contains("lowsaturation")) {
                alerted.set(true);
            }
        };

    AlertGenerator generator = new AlertGenerator(storage, handler);

        // retrieve the patient object that was created in storage
        Patient patient = storage.getAllPatients().stream().filter(p -> p.getPatientId() == 1).findFirst()
                .orElseThrow(() -> new AssertionError("Patient not found in storage"));

        generator.evaluateData(patient);

        assertTrue(alerted.get(), "Low saturation should trigger an alert via the handler");
        storage.clear();
    }
}
