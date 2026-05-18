package com.alerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.data_management.DataStorage;
import com.data_management.Patient;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private final DataStorage dataStorage;
    private final AlertHandler handler;
    private final List<AlertStrategy> strategies = new ArrayList<>();

    public AlertGenerator(DataStorage dataStorage) {
        this(dataStorage, null);
    }

    public AlertGenerator(DataStorage dataStorage, AlertHandler handler) {
        this(dataStorage, handler, null);
    }

    /**
     * Constructs an AlertGenerator with an explicit strategy list (useful for tests)
     */
    public AlertGenerator(DataStorage dataStorage, AlertHandler handler, List<AlertStrategy> strategies) {
        this.dataStorage = Objects.requireNonNull(dataStorage);
        this.handler = handler;
        if (strategies != null && !strategies.isEmpty()) {
            this.strategies.addAll(strategies);
        } else {
            // default strategies
            this.strategies.add(new BloodPressureStrategy());
            this.strategies.add(new HeartRateStrategy());
            this.strategies.add(new OxygenSaturationStrategy());
            // combined strategy for simultaneous low saturation + low systolic BP
            this.strategies.add(new HypoxicHypotensionStrategy());
        }
    }

    public void addStrategy(AlertStrategy s) {
        strategies.add(s);
    }

    /**
     * Evaluate patient data by delegating to registered strategies.
     */
    public void evaluateData(Patient patient) {
        for (AlertStrategy s : strategies) {
            s.checkAlert(patient, this);
        }
    }

    /**
     * Evaluate alert conditions for all patients stored in the configured data storage.
     */
    public void evaluateAll() {
        for (Patient patient : dataStorage.getAllPatients()) {
            evaluateData(patient);
        }
    }

    /**
     * Default trigger action: log the alert. In a fuller system this would notify staff or persist alerts.
     */
    private void triggerAlert(Alert alert) {
        Logger.getLogger(AlertGenerator.class.getName()).warning(
                String.format("ALERT patient=%s condition=%s timestamp=%d", alert.getPatientId(), alert.getCondition(), alert.getTimestamp()));
        if (handler != null) {
            handler.handle(alert);
        }
    }

    /**
     * Helper for strategies to create alerts through a factory and trigger them.
     */
    public void triggerViaFactory(AlertFactory factory, String patientId, String condition) {
        Alert a = factory.createAlert(patientId, condition, System.currentTimeMillis());
        triggerAlert(a);
    }

    
}
