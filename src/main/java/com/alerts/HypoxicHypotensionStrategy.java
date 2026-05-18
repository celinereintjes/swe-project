package com.alerts;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Detects simultaneous low saturation and low systolic blood pressure.
 */
public class HypoxicHypotensionStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        // look back 10 minutes for latest records
        List<PatientRecord> records = patient.getAllRecords();
        Optional<PatientRecord> latestSat = records.stream()
                .filter(r -> r.getRecordType().toLowerCase().contains("satur"))
                .max(Comparator.comparingLong(PatientRecord::getTimestamp));
        Optional<PatientRecord> latestBp = records.stream()
                .filter(r -> r.getRecordType().toLowerCase().contains("blood") || r.getRecordType().toLowerCase().contains("pressure"))
                .max(Comparator.comparingLong(PatientRecord::getTimestamp));

        if (latestSat.isPresent() && latestBp.isPresent()) {
            PatientRecord sat = latestSat.get();
            PatientRecord bp = latestBp.get();
            long dt = Math.abs(sat.getTimestamp() - bp.getTimestamp());
            if (dt <= 10 * 60_000L && sat.getMeasurementValue() < 92.0 && bp.getMeasurementValue() < 90.0) {
                generator.triggerViaFactory(new AlertFactory() {
                    @Override
                    public Alert createAlert(String patientId, String condition, long timestamp) {
                        return new BasicAlert(patientId, condition, timestamp);
                    }
                }, String.valueOf(patient.getPatientId()), "HypoxicHypotension");
            }
        }
    }
}
