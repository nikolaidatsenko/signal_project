package com.cardio_generator.outputs;

/**
 * Defines the contract for outputting patient data in health monitoring simulations.
 * Implementing classes must provide a method to output patient data, including patient ID,
 * timestamp, label, and data.
 */
public interface OutputStrategy {
    void output(int patientId, long timestamp, String label, String data);
}
