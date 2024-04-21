package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the behavior for generating patient data.
 * Implementing classes must provide a method to generate patient data based on the given patient ID
 * and output strategy.
 */
public interface PatientDataGenerator {
    void generate(int patientId, OutputStrategy outputStrategy);
}
