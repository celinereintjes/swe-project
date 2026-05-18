package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

public class ECGDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private final  double[] lastEcgValues;
    private static final double PI = Math.PI;

    public ECGDataGenerator(int patientCount) {
        lastEcgValues = new double[patientCount + 1];
        // Initialize the last ECG value for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastEcgValues[i] = 0; // Initial ECG value can be set to 0
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        // TODO Check how realistic this data is and make it more realistic if necessary
        double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);
        outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));
        lastEcgValues[patientId] = ecgValue;
    }

    private double simulateEcgWaveform(int patientId, double lastEcgValue) {
        // Simplified ECG waveform generation based on sinusoids
        double hr = 60.0 + random.nextDouble() * 20.0; // Simulate heart rate variability between 60 and 80 bpm
        double t = System.currentTimeMillis() / 1000.0; // Use system time to simulate continuous time
        double ecgFrequency = hr / 60.0; // Convert heart rate to Hz
        double phaseShift = patientId * 0.15; // Unique phase for each patient

        // Simulate different components of the ECG signal
        double pWave = 0.1 * Math.sin(2 * PI * ecgFrequency * t + phaseShift);
        double qrsComplex = 0.5 * Math.sin(2 * PI * 3 * ecgFrequency * t + phaseShift);
        double tWave = 0.2 * Math.sin(2 * PI * 2 * ecgFrequency * t + PI / 4 + phaseShift);
        double smoothing = 0.05 * lastEcgValue;

        return pWave + qrsComplex + tWave + smoothing + random.nextDouble() * 0.05; // Add small noise
    }
}
