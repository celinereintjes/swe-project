package com.cardio_generator;

import java.io.IOException;

/**
 * Small entry point that routes to different runners depending on the first argument.
 * Usage: java -jar app.jar [storage|simulator] [args...]
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: Main [storage|simulator] [...]");
            return;
        }
        switch (args[0]) {
            case "storage":
                // Example: storage <directory>
                com.data_management.DataStorage storage = com.data_management.DataStorage.getInstance();
                storage.clear();
                if (args.length > 1) {
                    com.data_management.FileDataReader reader = new com.data_management.FileDataReader(args[1]);
                    reader.readData(storage);
                }
                break;
            case "simulator":
            default:
                HealthDataSimulator.main(java.util.Arrays.copyOfRange(args, 1, args.length));
                break;
        }
    }
}
