package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Output strategy that writes generated events to files in a base directory.
 *
 * <p>Naming and visibility follow the Google Java Style Guide: instance fields
 * use lowerCamelCase, constants (if any) use ALL_CAPS, and fields are private
 * unless intended to be exposed.</p>
 */
public class FileOutputStrategy implements OutputStrategy {

    /** Base directory where output files are written. */
    // Correction: use lowerCamelCase for instance fields per Google Java Style.
    // See: https://google.github.io/styleguide/javaguide.html#s3.3.1-field-names
    private final String baseDirectory;

    /**
     * Map that caches the file path per label. Use ConcurrentHashMap for
     * thread-safety when multiple generators write concurrently.
     */
    // Correction: declare field as the interface type (Map) and use a
    // thread-safe implementation. Google style favors using interface types
    // for declarations. See: https://google.github.io/styleguide/javaguide.html#s3.3.3-field-declarations
    private final Map<String, String> fileMap = new ConcurrentHashMap<>();

    // Logger: prefer structured logging instead of printing to System.err.
    // Correction: added java.util.logging.Logger per recommended practice.
    // See: https://google.github.io/styleguide/javaguide.html#s5.3-logging
    private static final Logger LOGGER = Logger.getLogger(FileOutputStrategy.class.getName());

    /**
     * Creates a new FileOutputStrategy that writes files into the given
     * directory.
     *
     * @param baseDirectory directory where output files will be created
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes a single record to the file corresponding to the label. If the
     * directory or file does not exist it will be created. Errors are logged
     * and the method does not throw checked exceptions to callers.
     *
     * @param patientId patient identifier
     * @param timestamp epoch milliseconds when the data was generated
     * @param label     label used to group records into files (one file per label)
     * @param data      the payload string to write
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Ensure base directory exists.
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            // Correction: log the exception instead of printing to stderr.
            LOGGER.log(Level.SEVERE, "Error creating base directory: " + baseDirectory, e);
            return;
        }

        // Compute file path for the given label (cached).
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file; use BufferedWriter via Files.newBufferedWriter.
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (IOException e) {
            // Correction: log write failures with the exception for diagnostics.
            LOGGER.log(Level.SEVERE, "Error writing to file " + filePath, e);
        }
    }
}