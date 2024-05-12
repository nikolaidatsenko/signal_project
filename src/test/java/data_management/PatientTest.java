package data_management;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientTest {

    @Test
    void testGetRecords() {
        Patient patient1 = new Patient(1);

        // Add records to simulate different alert conditions for patient 1
        patient1.addRecord(170,"SystolicPressure", 1621453100);// I will try to exclude this
        patient1.addRecord(150,"SystolicPressure", 1621453200);
        patient1.addRecord(120,"SystolicPressure", 1621453300);
        patient1.addRecord(120,"SystolicPressure", 1621453400);// And this so we expect only the two above

        assertEquals(2, patient1.getRecords(1621453200,1621453300).size()); // Validate if we do get 2 records
    }
}
