package cardio_generator;

import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HealthDataSimulatorSingletonTest {

    @Test
    void testSingletonInstance() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();

        // Check that both instances are the same
        assertSame(instance1, instance2, "Both instances should be the same");
    }

    @Test
    void testSingletonInstanceNotNull() {
        HealthDataSimulator instance = HealthDataSimulator.getInstance();

        // Check that the instance is not null
        assertNotNull(instance, "Instance should not be null");
    }

    @Test
    void testSimulationExecution() throws IOException {
        HealthDataSimulator instance = HealthDataSimulator.getInstance();

        // Call the main method to execute the simulation
        String[] args = {"--patient-count", "10", "--output", "console"};
        instance.main(args);

        // Since this is a singleton, we cannot test internal state changes easily,
        // but we can ensure the instance was used without throwing exceptions.
        assertNotNull(instance, "Simulation should execute without issues");
    }
}
