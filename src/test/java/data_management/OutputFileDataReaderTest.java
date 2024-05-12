package data_management;

import com.data_management.DataStorage;
import com.data_management.OutputFileDataReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputFileDataReaderTest {

    @Test
    void testReadFile() throws IOException {

        DataStorage storage = new DataStorage();

        OutputFileDataReader reader = new OutputFileDataReader("--output file:src/test/testOutput1.txt");
        reader.readData(storage);

        List<PatientRecord> recordsP1 = storage.getRecords(1, 1621453200, 1621453800);
        List<PatientRecord> recordsP2 = storage.getRecords(2, 1621453200, 1621453800);
        assertEquals(2, recordsP1.size()); // Check if two records are retrieved for patientId 1
        assertEquals(0.54, recordsP1.get(0).getMeasurementValue()); // Validate records of patient1
        assertEquals(0.55, recordsP1.get(1).getMeasurementValue());
        assertEquals(95.3, recordsP2.get(0).getMeasurementValue()); // Validate record of patient2
    }
}
