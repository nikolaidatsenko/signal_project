package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts; // added a List of generated alerts to be able to test and see if the expected alerts are generated

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the {@link #triggerAlert}
     * method. This method should define the specific conditions under which an alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        evaluateBloodPressure(patient.getAllRecords(), patient.getId());
        evaluateSaturation(patient.getAllRecords(), patient.getId());
        evaluateECG(patient.getAllRecords(), patient.getId());
        evaluateHypotensiveHypoxemia(patient.getAllRecords(), patient.getId());
    }

    /**
     * Checks blood pressure readings for both critical threshold and trend alerts.
     *
     * @param records    the patient's medical records to evaluate
     * @param patientId  the ID of the patient being evaluated
     */
    private void evaluateBloodPressure(List<PatientRecord> records, String patientId) {
        List<Double> systolicValues = new ArrayList<>();
        List<Double> diastolicValues = new ArrayList<>();

        for (PatientRecord record : records) {
            switch (record.getRecordType()) {
                case "SystolicPressure":
                    systolicValues.add(record.getMeasurementValue());
                    if (systolicValues.size() > 2) { // Ensure there are at least three elements before processing
                        if (checkTrendConditions(systolicValues)) {
                            triggerAlert(new Alert(patientId, "Trend Alert: Systolic Blood Pressure Trend Detected", record.getTimestamp()));
                            systolicValues.remove(0); // Move the window
                        }
                    }
                    break;
                case "DiastolicPressure":
                    diastolicValues.add(record.getMeasurementValue());
                    if (diastolicValues.size() > 2) {
                        if (checkTrendConditions(diastolicValues)) {
                            triggerAlert(new Alert(patientId, "Trend Alert: Diastolic Blood Pressure Trend Detected", record.getTimestamp()));
                            diastolicValues.remove(0); // Move the window
                        }
                    }
                    break;
            }
        }

        // Handle critical threshold alert
        for(PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure")) {
                if (record.getMeasurementValue() > 180 || record.getMeasurementValue() < 90){
                    triggerAlert(new Alert(patientId, "Critical Threshold Alert: Blood Pressure Out of Range", record.getTimestamp()));
                }
            }
            if (record.getRecordType().equals("DiastolicPressure")) {
                if (record.getMeasurementValue() > 120 || record.getMeasurementValue() < 60) {
                    triggerAlert(new Alert(patientId, "Critical Threshold Alert: Blood Pressure Out of Range", record.getTimestamp()));
                }
            }
        }
    }

    /**
     * Checks saturation readings for both low saturation and rapid drop alerts.
     *
     * @param records    the patient's medical records to evaluate
     * @param patientId  the ID of the patient being evaluated
     */
    private void evaluateSaturation(List<PatientRecord> records, String patientId) {
        List<Double> window = new ArrayList<>();
        int startIndex = 0;

        // Handle rapid drop alert
        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if (record.getRecordType().equals("Saturation")) {
                window.add(record.getMeasurementValue());
                if (i - startIndex == 9) {
                    if (checkRapidDrop(window)) {
                        triggerAlert(new Alert(patientId, "Rapid Drop Alert: Blood Saturation Level Fell Rapidly", record.getTimestamp()));
                    }
                    window.remove(0);
                    startIndex++;
                }
            }
        }

        // Handle low saturation alert
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation")) {
                if (record.getMeasurementValue() < 92) {
                    triggerAlert(new Alert(patientId, "Low Saturation Alert: Blood Saturation Level Too Low", record.getTimestamp()));
                }
            }
        }
    }

    /**
     * Checks ECG readings for both abnormal heart rate and irregular beat alerts.
     *
     * @param records    the patient's medical records to evaluate
     * @param patientId  the ID of the patient being evaluated
     */
    private void evaluateECG(List<PatientRecord> records, String patientId) {
        // Handle abnormal heart rate alert
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("ECG")) {
                double heartRate = record.getMeasurementValue();
                if (heartRate > 100 || heartRate < 50) {
                    triggerAlert(new Alert(patientId, "Abnormal Heart Rate Alert: Heart Rate Out of Range", record.getTimestamp()));
                }
            }
        }

        // Handle irregular beat alert
        List<Double> rrIntervals = new ArrayList<>();

        // Calculate RR intervals
        for (int i = 1; i < records.size(); i++) {
            if (records.get(i).getRecordType().equals("ECG")) {
                double rrInterval = records.get(i).getTimestamp() - records.get(i - 1).getTimestamp();
                rrIntervals.add(rrInterval);
            }
        }

        // Calculate standard deviation of RR intervals
        double meanRRInterval = rrIntervals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double sumSquaredDeviations = rrIntervals.stream().mapToDouble(rrInterval -> Math.pow(rrInterval - meanRRInterval, 2)).sum();
        double standardDeviation = Math.sqrt(sumSquaredDeviations / (rrIntervals.size() - 1));

        // Check if standard deviation exceeds threshold
        if (standardDeviation > 0.1) {
            triggerAlert(new Alert(patientId, "Irregular Beat Alert: Abnormal Consecutive Beat Intervals Detected", records.get(records.size() - 1).getTimestamp()));
        }
    }

    /**
     * Checks for combined low blood pressure and low blood oxygen saturation levels.
     * Triggers a "Hypotensive Hypoxemia Alert" if both conditions are met.
     *
     * @param records    the patient's medical records to evaluate
     * @param patientId  the ID of the patient being evaluated
     */
    private void evaluateHypotensiveHypoxemia(List<PatientRecord> records, String patientId) {
        boolean lowBloodPressure = false;
        boolean lowSaturation = false;

        // Check for low blood pressure and low saturation
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure") && record.getMeasurementValue() < 90) {
                lowBloodPressure = true;
            }
            if (record.getRecordType().equals("Saturation") && record.getMeasurementValue() < 92) {
                lowSaturation = true;
            }
        }

        // Trigger alert if both conditions are met
        if (lowBloodPressure && lowSaturation) {
            // Trigger the alert
            long timestamp = System.currentTimeMillis(); // Assuming the current time is used for the alert
            triggerAlert(new Alert(patientId, "Combined Alert: Hypotensive Hypoxemia Alert", timestamp));
        }
    }

    /**
     * Checks for consecutive readings with an increase or decrease greater than 10 mmHg.
     *
     * @param values   a list of systolic or diastolic blood pressure values
     * @return true if the consecutive readings show a consistent increase or decrease of greater than 10 mmHg, otherwise false
     */
    private boolean checkTrendConditions(List<Double> values) {
        if (values.size() < 3) {
            return false;
        }
        // Check for increasing or decreasing trends
        boolean increasingTrend = values.get(2) - values.get(1) > 10 && values.get(1) - values.get(0) > 10;
        boolean decreasingTrend = values.get(0) - values.get(1) > 10 && values.get(1) - values.get(2) > 10;
        return increasingTrend || decreasingTrend;
    }

    /**
     * Checks for a drop in blood saturation of 5% or more within a 10 minute interval.
     *
     * @param readings the list of blood saturation readings to check
     * @return true if such a drop is detected, otherwise false
     */
    private boolean checkRapidDrop(List<Double> readings) {
        Double start = readings.get(0);
        Double end = readings.get(readings.size() - 1);
        return start - end >= 5;
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        alerts.add(alert);
    }

    public int getAlertCount() {
        return alerts.size();
    }

    public List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    /**
     * Retrieves all alerts associated with a specific patient.
     *
     * @param patientId The ID of the patient for whom to retrieve alerts.
     * @return A list of alerts for the specified patient.
     */
    public List<Alert> getAlertsByPatientId(String patientId) {
        return alerts.stream()
                .filter(alert -> alert.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
}
