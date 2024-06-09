package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.WebSocketClient;
import com.data_management.PatientRecord;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class WebSocketClientTest {

    @Test
    public void test_successful_connection_to_websocket_server() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Connect to the WebSocket server
        webSocketClient.connect(serverUri);

        // Verify connection
        assertTrue(webSocketClient.isClientOpen());
        webSocketClient.close();
    }

    @Test
    public void test_connection_with_invalid_uri() throws InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String invalidUri = "invalid_uri";

        // Prepare latch to wait for error callback
        CountDownLatch latch = new CountDownLatch(1);
        webSocketClient.setOnErrorCallback(ex -> latch.countDown());

        // Attempt to connect with an invalid URI
        try {
            webSocketClient.connect(invalidUri);
        } catch (IOException e) {
            // Expected exception
        }

        // Wait for onError to be called
        boolean errorOccurred = latch.await(2, TimeUnit.SECONDS);

        // Verify error handling
        assertTrue("Expected onError to be called with invalid URI", errorOccurred);
        assertFalse("Client should not be open with invalid URI", webSocketClient.isClientOpen());
    }

    @Test
    public void test_handle_valid_message() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Prepare latch to wait for message handling
        CountDownLatch latch = new CountDownLatch(1);
        webSocketClient.connect(serverUri);

        // Simulate receiving a valid message
        String validMessage = "1,1627848284,label,123.45";
        webSocketClient.simulateMessage(validMessage);
        latch.countDown();

        // Wait for the message to be handled
        latch.await(2, TimeUnit.SECONDS);

        // Verify that data is correctly stored
        List<PatientRecord> records = dataStorage.getRecords(1, 1627848284L, 1627848284L);
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(123.45, records.get(0).getMeasurementValue(), 0.01);
        webSocketClient.close();
    }

    @Test
    public void test_handle_invalid_message() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Prepare latch to wait for message handling
        CountDownLatch latch = new CountDownLatch(1);
        webSocketClient.connect(serverUri);

        // Simulate receiving an invalid message
        String invalidMessage = "invalid message format";
        webSocketClient.simulateMessage(invalidMessage);
        latch.countDown();

        // Wait for the message to be handled
        latch.await(2, TimeUnit.SECONDS);

        // Verify that no data is stored
        List<PatientRecord> records = dataStorage.getRecords(1, 0, Long.MAX_VALUE);
        assertTrue(records.isEmpty());
        webSocketClient.close();
    }

    @Test
    public void test_reconnection_logic() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Connect and verify
        webSocketClient.connect(serverUri);
        assertTrue(webSocketClient.isClientOpen());

        // Close and verify
        webSocketClient.close();
        assertFalse(webSocketClient.isClientOpen());

        // Reconnect and verify
        webSocketClient.connect(serverUri);
        assertTrue(webSocketClient.isClientOpen());
        webSocketClient.close();
    }

    @Test
    public void test_multiple_messages() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Prepare latch to wait for message handling
        CountDownLatch latch = new CountDownLatch(2);
        webSocketClient.connect(serverUri);

        // Simulate receiving multiple valid messages
        String validMessage1 = "1,1627848284,label,123.45";
        String validMessage2 = "2,1627848285,label,67.89";
        webSocketClient.simulateMessage(validMessage1);
        latch.countDown();
        webSocketClient.simulateMessage(validMessage2);
        latch.countDown();

        // Wait for the messages to be handled
        latch.await(2, TimeUnit.SECONDS);

        // Verify that data is correctly stored
        List<PatientRecord> records1 = dataStorage.getRecords(1, 1627848284L, 1627848284L);
        List<PatientRecord> records2 = dataStorage.getRecords(2, 1627848285L, 1627848285L);

        assertNotNull(records1);
        assertEquals(1, records1.size());
        assertEquals(123.45, records1.get(0).getMeasurementValue(), 0.01);

        assertNotNull(records2);
        assertEquals(1, records2.size());
        assertEquals(67.89, records2.get(0).getMeasurementValue(), 0.01);

        webSocketClient.close();
    }

    @Test
    public void test_handle_large_message() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Prepare latch to wait for message handling
        CountDownLatch latch = new CountDownLatch(1);
        webSocketClient.connect(serverUri);

        // Generate a large message
        StringBuilder largeMessage = new StringBuilder("1,1627848284,label,");
        for (int i = 0; i < 10000; i++) {
            largeMessage.append("123.45");
        }
        webSocketClient.simulateMessage(largeMessage.toString());
        latch.countDown();

        // Wait for the message to be handled
        latch.await(2, TimeUnit.SECONDS);

        // Verify that the client is still open and responsive
        assertTrue(webSocketClient.isClientOpen());
        webSocketClient.close();
    }

    @Test
    public void test_disconnection_and_reconnection() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Connect and verify
        webSocketClient.connect(serverUri);
        assertTrue(webSocketClient.isClientOpen());

        // Close and verify
        webSocketClient.close();
        assertFalse(webSocketClient.isClientOpen());

        // Reconnect and verify
        webSocketClient.connect(serverUri);
        assertTrue(webSocketClient.isClientOpen());
        webSocketClient.close();
    }

    @Test
    public void test_handle_edge_case_messages() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Prepare latch to wait for message handling
        CountDownLatch latch = new CountDownLatch(2);
        webSocketClient.connect(serverUri);

        // Simulate receiving edge case messages
        String emptyMessage = "";
        String unexpectedFormatMessage = "unexpected,format";
        webSocketClient.simulateMessage(emptyMessage);
        latch.countDown();
        webSocketClient.simulateMessage(unexpectedFormatMessage);
        latch.countDown();

        // Wait for the messages to be handled
        latch.await(2, TimeUnit.SECONDS);

        // Verify that no data is stored
        List<PatientRecord> records = dataStorage.getRecords(1, 0, Long.MAX_VALUE);
        assertTrue(records.isEmpty());

        webSocketClient.close();
    }

    @Test
    public void test_alert_generation_logic() throws URISyntaxException, IOException, InterruptedException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String serverUri = "wss://echo.websocket.org";

        // Prepare latch to wait for message handling
        CountDownLatch latch = new CountDownLatch(1);
        webSocketClient.connect(serverUri);

        // Simulate receiving a message that should trigger an alert
        String alertMessage = "1,1627848284,ECG,200.0";
        webSocketClient.simulateMessage(alertMessage);
        latch.countDown();

        // Wait for the message to be handled
        latch.await(2, TimeUnit.SECONDS);

        // Create an alert generator
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);

        // Evaluate data to potentially generate an alert
        List<Patient> allPatients = dataStorage.getAllPatients();
        if (allPatients.isEmpty()) {
            fail("No patients found in data storage.");
        }

        Patient patient = allPatients.get(0);
        alertGenerator.evaluateData(patient);

        // Verify that an alert is generated
        List<Alert> alerts = alertGenerator.getAlertsByPatientId(patient.getId());

        // Ensure that alerts were generated
        assertFalse("Expected at least one alert to be generated", alerts.isEmpty());

        // Check for the specific alert condition
        boolean found = alerts.stream()
                .anyMatch(alert -> "Abnormal Heart Rate Alert: Heart Rate Out of Range [Priority: High]".equals(alert.getCondition()));
        assertTrue("Expected alert for abnormal heart rate", found);

        webSocketClient.close();
    }

    @Test
    public void test_connection_timeout() throws URISyntaxException, IOException {
        // Setup
        DataStorage dataStorage = new DataStorage();
        WebSocketClient webSocketClient = new WebSocketClient(dataStorage);
        String timeoutUri = "ws://example.com:12345"; // Use an address that times out

        // Attempt to connect and expect a timeout
        try {
            webSocketClient.connect(timeoutUri);
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Connection timed out"));
        }

        // Verify that the client is not open
        assertFalse(webSocketClient.isClientOpen());
    }
}
