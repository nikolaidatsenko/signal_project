package factories;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertFactoryTest {

    @Test
    void testBloodPressureAlertFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "Critical Threshold Alert: Blood Pressure Out of Range", System.currentTimeMillis());
        assertNotNull(alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Critical Threshold Alert: Blood Pressure Out of Range", alert.getCondition());
    }

    @Test
    void testBloodOxygenAlertFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("1", "Low Saturation Alert: Blood Saturation Level Too Low", System.currentTimeMillis());
        assertNotNull(alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Low Saturation Alert: Blood Saturation Level Too Low", alert.getCondition());
    }

    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("1", "Abnormal Heart Rate Alert: Heart Rate Out of Range", System.currentTimeMillis());
        assertNotNull(alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Abnormal Heart Rate Alert: Heart Rate Out of Range", alert.getCondition());
    }
}
