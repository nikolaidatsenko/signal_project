package com.cardio_generator.generators;
//import statements together
import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    // randomGenerator name changed to follow naming convention for constants
    public static final Random RANDOM_GENERATOR = new Random();
    // changed name to be in camelCase and put comment above
    // false = resolved, true = pressed
    private boolean[] alertStates;

    // changed method order so the primary method is above the constructor
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                // 90% chance to resolve (put comment above)
                if (RANDOM_GENERATOR.nextDouble() < 0.9) {
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-Lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
            // The catch block should provide more specific exception handling, rather than catching a generic Exception
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }
}
