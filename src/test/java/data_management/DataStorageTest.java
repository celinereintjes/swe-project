package data_management;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

/**
 * Unit tests for DataStorage.getRecords(...) behavior.
 */
public class DataStorageTest {

    @Test
    void testGetRecordsEmpty() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        List<PatientRecord> records = storage.getRecords(1, 0L, 1000L);
        assertNotNull(records, "getRecords should not return null for empty storage");
        assertTrue(records.isEmpty(), "Expected no records");
        storage.clear();
    }

    @Test
    void testGetRecordsBoundary() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        storage.addPatientData(1, 100.0, "Test", 1000L);
        List<PatientRecord> records = storage.getRecords(1, 1000L, 1000L);
        assertEquals(1, records.size(), "Record exactly on boundary should be returned");
        assertEquals(100.0, records.get(0).getMeasurementValue());
        storage.clear();
    }

    @Test
    void testGetRecordsRange() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        storage.addPatientData(1, 100.0, "Test", 1000L);
        storage.addPatientData(1, 200.0, "Test", 2000L);
        List<PatientRecord> records = storage.getRecords(1, 1500L, 2500L);
        assertEquals(1, records.size(), "Should return records within range");
        assertEquals(200.0, records.get(0).getMeasurementValue());
        storage.clear();
    }
}
