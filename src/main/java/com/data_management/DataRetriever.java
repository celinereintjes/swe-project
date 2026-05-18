package com.data_management;

import java.util.List;

public class DataRetriever {
    private final DataStorage dataStorage;

    public DataRetriever(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public List<PatientRecord> retrieveData(int patientId, long startTime, long endTime) {
        return dataStorage.getRecords(patientId, startTime, endTime);
    }

    public List<Patient> getAllPatients() {
        return dataStorage.getAllPatients();
    }
}
