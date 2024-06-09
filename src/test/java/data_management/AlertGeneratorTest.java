package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.OutputFileDataReader;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertGeneratorTest {

    @Test
    void testAlertGeneration() throws IOException {
        Patient patient1 = new Patient(1);
        Patient patient2 = new Patient(2);

        // Add records to simulate different alert conditions for patient 1
        patient1.addRecord(170,"SystolicPressure", 1621453100);
        patient1.addRecord(150,"SystolicPressure", 1621453200);
        patient1.addRecord(120,"SystolicPressure", 1621453300); // should trigger a trend alert

        patient1.addRecord(95,"Saturation", 1621453100);
        patient1.addRecord(90,"Saturation", 1621453200); // should trigger a low saturation alert

        patient1.addRecord(115,"ECG", 1621453100); // should trigger an abnormal heart rate alert


        // Add records to simulate different alert conditions for patient 2
        // Add records to simulate a rapid drop for patient 2
        for (int i = 0; i <= 9; i++) {
            patient2.addRecord(96 - i, "Saturation", 1621453600 + i * 60); // Expected to trigger a rapid drop alert
        }

        // Add records for a combined alert (hypotensive hypoxemia) for patient 2 + Critical Threshold Alert: Blood Pressure Out of Range
        patient2.addRecord(85, "SystolicPressure", 1621453700); // Hypotensive condition
        patient2.addRecord(90, "Saturation", 1621453700); // Hypoxemic condition

        // Add irregular ECG data to patient 2
        patient2.addRecord(0.8, "ECG", 10000);
        patient2.addRecord(1.2, "ECG", 12000); // Expected to trigger an irregular beat alert

        patient2.addRecord(70,"DiastolicPressure", 1621453500);
        patient2.addRecord(90,"DiastolicPressure", 1621453600);
        patient2.addRecord(110,"DiastolicPressure", 1621453700); // should trigger a trend alert

        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        // Evaluate data to generate alerts
        alertGenerator.evaluateData(patient1); // This functions as a evaluateData Test as well
        alertGenerator.evaluateData(patient2);

        // Check total number of alerts generated
        assertEquals(15, alertGenerator.getAlertCount(), "Total alerts generated should match the expected amount.");

        // Check for alerts
        List<Alert> patientAlerts = alertGenerator.getAlertsByPatientId("1");

        assertEquals("Trend Alert: Systolic Blood Pressure Trend Detected [Priority: High]", patientAlerts.get(0).getCondition());
        assertEquals("Abnormal Heart Rate Alert: Heart Rate Out of Range [Priority: High]", patientAlerts.get(1).getCondition());
        assertEquals("Low Saturation Alert: Blood Saturation Level Too Low [Priority: High]", patientAlerts.get(2).getCondition());

        List<Alert> patient2Alerts = alertGenerator.getAlertsByPatientId("2");

        assertTrue(patient2Alerts.stream()
                .anyMatch(alert -> alert.getCondition().equals("Critical Threshold Alert: Blood Pressure Out of Range [Priority: High]")));
        assertTrue(patient2Alerts.stream()
                .anyMatch(alert -> alert.getCondition().equals("Rapid Drop Alert: Blood Saturation Level Fell Rapidly [Priority: High]")));
        assertTrue(patient2Alerts.stream()
                .anyMatch(alert -> alert.getCondition().equals("Irregular Beat Alert: Abnormal Consecutive Beat Intervals Detected [Priority: High]")));
        assertTrue(patient2Alerts.stream()
                .anyMatch(alert -> alert.getCondition().equals("Trend Alert: Diastolic Blood Pressure Trend Detected [Priority: High]")));

    }
}
