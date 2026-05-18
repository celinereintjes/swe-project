package com.data_management;

public class PatientData {
    private final int patientId;
    private final double measurementValue;
    private final String recordType;
    private final long timestamp;
    private final int version;

    public PatientData(int patientId, double measurementValue, String recordType, long timestamp, int version) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
        this.version = version;
    }

    public int getPatientId() {
        return patientId;
    }

    public double getMeasurementValue() {
        return measurementValue;
    }

    public String getRecordType() {
        return recordType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }
}
