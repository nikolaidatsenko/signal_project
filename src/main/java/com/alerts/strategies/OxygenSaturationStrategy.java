package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();
        List<Double> window = new ArrayList<>();
        int startIndex = 0;

        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if (record.getRecordType().equals("Saturation")) {
                window.add(record.getMeasurementValue());
                if (i - startIndex == 9) {
                    if (checkRapidDrop(window)) {
                        alerts.add(new Alert(patient.getId(), "Rapid Drop Alert: Blood Saturation Level Fell Rapidly", record.getTimestamp()));
                    }
                    window.remove(0);
                    startIndex++;
                }
            }
        }

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Saturation") && record.getMeasurementValue() < 92) {
                alerts.add(new Alert(patient.getId(), "Low Saturation Alert: Blood Saturation Level Too Low", record.getTimestamp()));
            }
        }

        return alerts;
    }

    private boolean checkRapidDrop(List<Double> readings) {
        Double start = readings.get(0);
        Double end = readings.get(readings.size() - 1);
        return start - end >= 5;
    }
}
