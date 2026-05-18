package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PatientDataReader implements DataReader {
    private final String filePath;

    public PatientDataReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int patientId = Integer.parseInt(parts[0]);
                double measurementValue = Double.parseDouble(parts[1]);
                String recordType = parts[2];
                long timestamp = Long.parseLong(parts[3]);
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        }
    }
    
    @Override
    public void connectWebSocket(String url, DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("WebSocket connection is not supported by PatientDataReader");
    }
}
