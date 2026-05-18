package com.alerts;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private final AlertFactory factory = new ECGAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        int pid = patient.getPatientId();
        List<PatientRecord> records = patient.getAllRecords().stream()
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp)).collect(Collectors.toList());
        if (records.isEmpty()) return;

        List<PatientRecord> ecgs = records.stream().filter(r -> r.getRecordType().equals("ECG")).collect(Collectors.toList());
        if (ecgs.size() < 2) return;
        int n = Math.min(10, ecgs.size() - 1);
        double sum = 0.0;
        for (int i = ecgs.size() - 1 - n; i < ecgs.size() - 1; i++) {
            if (i >= 0) sum += ecgs.get(i).getMeasurementValue();
        }
        double avg = sum / n;
        double newest = ecgs.get(ecgs.size() - 1).getMeasurementValue();
        if (avg > 0 && newest > 2.0 * avg) {
            generator.triggerViaFactory(factory, String.valueOf(pid), "ECGAbnormalPeak");
        }
    }
}
