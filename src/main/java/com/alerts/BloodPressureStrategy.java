package com.alerts;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    private static final Logger LOGGER = Logger.getLogger(BloodPressureStrategy.class.getName());
    private final AlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        int pid = patient.getPatientId();
        List<PatientRecord> records = patient.getAllRecords().stream()
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp)).collect(Collectors.toList());
        if (records.isEmpty()) return;

        List<PatientRecord> lastSystolic = records.stream().filter(r -> r.getRecordType().equals("SystolicPressure"))
                .collect(Collectors.toList());
        List<PatientRecord> lastDiastolic = records.stream().filter(r -> r.getRecordType().equals("DiastolicPressure"))
                .collect(Collectors.toList());

        if (!lastSystolic.isEmpty()) {
            double s = lastSystolic.get(lastSystolic.size() - 1).getMeasurementValue();
            if (s > 180 || s < 90) {
                generator.triggerViaFactory(factory, String.valueOf(pid), "BloodPressureCritical_Systolic");
                LOGGER.fine(() -> "Blood pressure critical systolic alert for patient " + pid + ": " + s);
            }
        }
        if (!lastDiastolic.isEmpty()) {
            double d = lastDiastolic.get(lastDiastolic.size() - 1).getMeasurementValue();
            if (d > 120 || d < 60) {
                generator.triggerViaFactory(factory, String.valueOf(pid), "BloodPressureCritical_Diastolic");
                LOGGER.fine(() -> "Blood pressure critical diastolic alert for patient " + pid + ": " + d);
            }
        }

        // Trend checks: last 3 readings
        checkBloodPressureTrend(pid, records.stream().filter(r -> r.getRecordType().equals("SystolicPressure")).collect(Collectors.toList()), generator, factory, "SystolicPressure");
        checkBloodPressureTrend(pid, records.stream().filter(r -> r.getRecordType().equals("DiastolicPressure")).collect(Collectors.toList()), generator, factory, "DiastolicPressure");
    }

    private void checkBloodPressureTrend(int pid, List<PatientRecord> values, AlertGenerator generator, AlertFactory factory, String label) {
        if (values.size() < 3) return;
        int size = values.size();
        double a = values.get(size - 3).getMeasurementValue();
        double b = values.get(size - 2).getMeasurementValue();
        double c = values.get(size - 1).getMeasurementValue();
        if ((b - a) > 10.0 && (c - b) > 10.0) {
            generator.triggerViaFactory(factory, String.valueOf(pid), label + "_IncreasingTrend");
        } else if ((a - b) > 10.0 && (b - c) > 10.0) {
            generator.triggerViaFactory(factory, String.valueOf(pid), label + "_DecreasingTrend");
        }
    }
}
