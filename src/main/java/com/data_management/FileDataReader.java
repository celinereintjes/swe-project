package com.data_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads simulator output files produced with the `--output file:<dir>` option
 * and populates a {@link DataStorage} instance.
 *
 * <p>Each input line is expected to match the format produced by
 * {@link com.cardio_generator.outputs.FileOutputStrategy#output}:
 * "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s".</p>
 */
public class FileDataReader implements DataReader {

    private static final Logger LOGGER = Logger.getLogger(FileDataReader.class.getName());

    private final Path directory;

    /**
     * Create a reader that will scan the provided directory for text files.
     *
     * @param directoryPath directory containing simulator output files
     */
    public FileDataReader(String directoryPath) {
        this.directory = Paths.get(directoryPath);
    }

    /**
     * Read all files in the configured directory and add parsed records to
     * the provided {@link DataStorage}.
     *
     * @param dataStorage destination storage
     * @throws IOException if there is an I/O problem accessing the directory or files
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IOException("Directory does not exist: " + directory.toString());
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.txt")) {
            for (Path file : stream) {
                processFile(file, dataStorage);
            }
        }
    }

    @Override
    public void connectWebSocket(String url, DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("WebSocket connection is not supported by FileDataReader");
    }

    private void processFile(Path file, DataStorage storage) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                try {
                    ParsedRecord record = parseLine(line);
                    if (record != null) {
                        storage.addPatientData(record.patientId, record.value, record.label, record.timestamp);
                    }
                } catch (IllegalArgumentException e) {
                    // Malformed line — log and continue with next line (graceful handling).
                    LOGGER.log(Level.WARNING, String.format("Malformed line in %s:%d -> %s", file.getFileName(), lineNo, line), e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read file: " + file.toString(), e);
        }
    }

    /**
     * Parses a single line produced by FileOutputStrategy.
     * Expected format: "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s"
     *
     * @param line input line
     * @return ParsedRecord or null if the line is empty/ignored
     */
    private ParsedRecord parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        // Split on comma to find the 4 segments
        // Expected segments: ["Patient ID: X", " Timestamp: Y", " Label: Z", " Data: V"]
        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Line does not contain 4 comma-separated parts");
        }

        // Parse patientId
        String p0 = parts[0].trim(); // "Patient ID: X"
        if (!p0.startsWith("Patient ID:")) {
            throw new IllegalArgumentException("Missing 'Patient ID:' prefix");
        }
        int patientId = Integer.parseInt(p0.substring("Patient ID:".length()).trim());

        // Parse timestamp
        String p1 = parts[1].trim(); // "Timestamp: Y"
        if (!p1.startsWith("Timestamp:")) {
            throw new IllegalArgumentException("Missing 'Timestamp:' prefix");
        }
        long timestamp = Long.parseLong(p1.substring("Timestamp:".length()).trim());

        // Parse label
        String p2 = parts[2].trim(); // "Label: Z"
        if (!p2.startsWith("Label:")) {
            throw new IllegalArgumentException("Missing 'Label:' prefix");
        }
        String label = p2.substring("Label:".length()).trim();

        // Parse data/value — remainder of the line after the third comma
        // Join parts[3..] back together in case the data contains commas.
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append(parts[3].trim());
        for (int i = 4; i < parts.length; i++) {
            dataBuilder.append(",").append(parts[i]);
        }
        String dataPart = dataBuilder.toString();
        if (!dataPart.startsWith("Data:")) {
            throw new IllegalArgumentException("Missing 'Data:' prefix");
        }
        String valueStr = dataPart.substring("Data:".length()).trim();

        double value = Double.parseDouble(valueStr);

        return new ParsedRecord(patientId, timestamp, label, value);
    }

    private static class ParsedRecord {
        final int patientId;
        final long timestamp;
        final String label;
        final double value;

        ParsedRecord(int patientId, long timestamp, String label, double value) {
            this.patientId = patientId;
            this.timestamp = timestamp;
            this.label = label;
            this.value = value;
        }
    }
}
