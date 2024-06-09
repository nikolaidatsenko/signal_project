package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    void testSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();

        // Check that both instances are the same
        assertSame(instance1, instance2, "Both instances should be the same");
    }

    @Test
    void testSingletonInstanceNotNull() {
        DataStorage instance = DataStorage.getInstance();

        // Check that the instance is not null
        assertNotNull(instance, "Instance should not be null");
    }

    @Test
    void testSingletonDataConsistency() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();

        // Add some data to instance1
        instance1.addPatientData(1, 98.6, "Temperature", 1621453200);

        // Verify that the data is also present in instance2
        assertFalse(instance2.getRecords(1, 1621453200, 1621453200).isEmpty(), "Data should be consistent across instances");
    }
}
