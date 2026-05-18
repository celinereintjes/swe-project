package com.alerts;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private final AlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        int pid = patient.getPatientId();
        List<PatientRecord> records = patient.getAllRecords().stream()
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp)).collect(Collectors.toList());
        if (records.isEmpty()) return;

        List<PatientRecord> sats = records.stream().filter(r -> r.getRecordType().equals("Saturation")).collect(Collectors.toList());
        if (!sats.isEmpty()) {
            double latest = sats.get(sats.size() - 1).getMeasurementValue();
            if (latest < 92.0) {
                generator.triggerViaFactory(factory, String.valueOf(pid), "LowSaturation");
            }
        }

        // Rapid drop: any two within 10 minutes drop >=5
        for (int i = 0; i < sats.size(); i++) {
            for (int j = i + 1; j < sats.size(); j++) {
                PatientRecord earlier = sats.get(i);
                PatientRecord later = sats.get(j);
                long dt = later.getTimestamp() - earlier.getTimestamp();
                if (dt <= 10 * 60_000L && (earlier.getMeasurementValue() - later.getMeasurementValue()) >= 5.0) {
                    generator.triggerViaFactory(factory, String.valueOf(pid), "RapidSaturationDrop");
                    return;
                }
            }
        }
    }
}
