package strategies;

import com.alerts.Alert;
import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AlertStrategyTest {

    @Test
    void testBloodPressureStrategy() {
        Patient patient = new Patient(1);
        patient.addRecord(170, "SystolicPressure", 1621453100);
        patient.addRecord(150, "SystolicPressure", 1621453200);
        patient.addRecord(120, "SystolicPressure", 1621453300);

        AlertStrategy strategy = new BloodPressureStrategy();
        List<Alert> alerts = strategy.checkAlert(patient);

        assertEquals("Trend Alert: Systolic Blood Pressure Trend Detected", alerts.get(0).getCondition());
    }

    @Test
    void testHeartRateStrategy() {
        Patient patient = new Patient(1);
        patient.addRecord(115, "ECG", 1621453100);
        patient.addRecord(0.8, "ECG", 10000);
        patient.addRecord(1.2, "ECG", 12000);

        AlertStrategy strategy = new HeartRateStrategy();
        List<Alert> alerts = strategy.checkAlert(patient);

        assertEquals("Abnormal Heart Rate Alert: Heart Rate Out of Range", alerts.get(0).getCondition());
        assertEquals("Irregular Beat Alert: Abnormal Consecutive Beat Intervals Detected", alerts.get(3).getCondition());
    }

    @Test
    void testOxygenSaturationStrategy() {
        Patient patient = new Patient(1);
        patient.addRecord(95, "Saturation", 1621453100);
        patient.addRecord(90, "Saturation", 1621453200);
        for (int i = 0; i <= 9; i++) {
            patient.addRecord(96 - i, "Saturation", 1621453600 + i * 60);
        }

        AlertStrategy strategy = new OxygenSaturationStrategy();
        List<Alert> alerts = strategy.checkAlert(patient);

        assertEquals("Rapid Drop Alert: Blood Saturation Level Fell Rapidly", alerts.get(1).getCondition());
        assertEquals("Low Saturation Alert: Blood Saturation Level Too Low", alerts.get(2).getCondition());
    }
}
