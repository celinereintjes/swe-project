package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates alert events for patients.
 *
 * <p>Style notes: field and variable names follow the Google Java Style Guide
 * (https://google.github.io/styleguide/javaguide.html) — constants are
 * {@code private static final} in ALL_CAPS and instance fields use
 * lowerCamelCase.</p>
 */
public class AlertGenerator implements PatientDataGenerator {

    /** Random number generator (private per Google style; not exposed). */
    // Correction: made RNG a private static final constant following Google Java
    // Style Guide (constants are static final and use ALL_CAPS). See:
    // https://google.github.io/styleguide/javaguide.html#s3.4.2-constant-names
    private static final Random RANDOM_GENERATOR = new Random();

    /** Alert states per patient index: false = resolved, true = active/pressed. */
    // Correction: field name uses lowerCamelCase and is declared private final
    // following Google Java Style (field naming and immutability preference).
    // See: https://google.github.io/styleguide/javaguide.html#s3.3.1-field-names
    private final boolean[] alertStates;

    /**
     * Constructs an AlertGenerator for the given number of patients.
     *
     * @param patientCount number of patients to simulate (ids assumed in range 0..patientCount)
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generate alert state for the given patient and send output via the provided
     * strategy.
     *
     * @param patientId      patient identifier (index into internal state)
     * @param outputStrategy destination/output strategy to receive generated events
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        if (alertStates[patientId]) {
            // 90% chance to resolve an active alert in this period
            if (RANDOM_GENERATOR.nextDouble() < 0.9) {
                alertStates[patientId] = false;
                outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
            }
        } else {
            // Use a small lambda rate to model occasional alerts.
            double lambdaRate = 0.1; // Average rate (alerts per period)
            // Probability of at least one event in the period: 1 - exp(-lambda).
            double p = -Math.expm1(-lambdaRate);
            boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

            if (alertTriggered) {
                alertStates[patientId] = true;
                outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
            }
        }
    }
}
