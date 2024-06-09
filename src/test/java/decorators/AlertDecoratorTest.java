package decorators;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertDecoratorTest {

    @Test
    void testRepeatedAlertDecorator() {
        Alert baseAlert = new Alert("1", "Blood Pressure Out of Range", System.currentTimeMillis());
        RepeatedAlertDecorator repeatedAlert = new RepeatedAlertDecorator(baseAlert, 60000); // 60 seconds interval

        // Simulate time passing by setting lastChecked to a time far enough in the past
        repeatedAlert.setLastChecked(System.currentTimeMillis() - 60001);

        assertEquals("Blood Pressure Out of Range (rechecked)", repeatedAlert.getCondition());
    }

    @Test
    void testPriorityAlertDecorator() {
        Alert baseAlert = new Alert("1", "Blood Pressure Out of Range", System.currentTimeMillis());
        Alert priorityAlert = new PriorityAlertDecorator(baseAlert, "High");
        assertEquals("Blood Pressure Out of Range [Priority: High]", priorityAlert.getCondition());
    }

    @Test
    void testAlertGeneratorWithDecorators() {
        Patient patient = new Patient(1);
        patient.addRecord(170, "SystolicPressure", 1621453100);
        patient.addRecord(150, "SystolicPressure", 1621453200);
        patient.addRecord(120, "SystolicPressure", 1621453300);

        DataStorage dataStorage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        alertGenerator.evaluateData(patient);
        Alert alert = alertGenerator.getAlertsByPatientId("1").get(0);
        assertEquals("Trend Alert: Systolic Blood Pressure Trend Detected [Priority: High]", alert.getCondition());
    }
}
