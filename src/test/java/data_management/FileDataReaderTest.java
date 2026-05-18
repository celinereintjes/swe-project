package data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

class FileDataReaderTest {

    @Test
    void parseWellFormedFile() throws IOException {
        Path dir = Files.createTempDirectory("sim-output-test");
        Path file = dir.resolve("out1.txt");
        long ts = System.currentTimeMillis();
        String line = String.format("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s", 42, ts, "Saturation", "95.0");
        Files.writeString(file, line + "\n");

    DataStorage storage = DataStorage.getInstance();
    storage.clear();
        FileDataReader reader = new FileDataReader(dir.toString());
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(42, ts - 10, ts + 10);
        assertEquals(1, records.size());
        assertEquals(95.0, records.get(0).getMeasurementValue());

        // cleanup
        Files.deleteIfExists(file);
        Files.deleteIfExists(dir);
        storage.clear();
    }
}
