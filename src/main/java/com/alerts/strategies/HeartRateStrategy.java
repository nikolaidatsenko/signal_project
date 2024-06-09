package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();
        List<Double> rrIntervals = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("ECG") && (record.getMeasurementValue() > 100 || record.getMeasurementValue() < 50)) {
                alerts.add(new Alert(patient.getId(), "Abnormal Heart Rate Alert: Heart Rate Out of Range", record.getTimestamp()));
            }
        }

        for (int i = 1; i < records.size(); i++) {
            if (records.get(i).getRecordType().equals("ECG")) {
                double rrInterval = records.get(i).getTimestamp() - records.get(i - 1).getTimestamp();
                rrIntervals.add(rrInterval);
            }
        }

        double meanRRInterval = rrIntervals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double sumSquaredDeviations = rrIntervals.stream().mapToDouble(rrInterval -> Math.pow(rrInterval - meanRRInterval, 2)).sum();
        double standardDeviation = Math.sqrt(sumSquaredDeviations / (rrIntervals.size() - 1));

        if (standardDeviation > 0.1) {
            alerts.add(new Alert(patient.getId(), "Irregular Beat Alert: Abnormal Consecutive Beat Intervals Detected", records.get(records.size() - 1).getTimestamp()));
        }

        return alerts;
    }
}
