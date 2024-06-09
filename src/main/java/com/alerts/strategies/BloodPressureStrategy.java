package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();
        List<Double> systolicValues = new ArrayList<>();
        List<Double> diastolicValues = new ArrayList<>();

        for (PatientRecord record : records) {
            switch (record.getRecordType()) {
                case "SystolicPressure":
                    systolicValues.add(record.getMeasurementValue());
                    if (systolicValues.size() > 2) {
                        if (checkTrendConditions(systolicValues)) {
                            alerts.add(new Alert(patient.getId(), "Trend Alert: Systolic Blood Pressure Trend Detected", record.getTimestamp()));
                            systolicValues.remove(0);
                        }
                    }
                    break;
                case "DiastolicPressure":
                    diastolicValues.add(record.getMeasurementValue());
                    if (diastolicValues.size() > 2) {
                        if (checkTrendConditions(diastolicValues)) {
                            alerts.add(new Alert(patient.getId(), "Trend Alert: Diastolic Blood Pressure Trend Detected", record.getTimestamp()));
                            diastolicValues.remove(0);
                        }
                    }
                    break;
            }
        }

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure") && (record.getMeasurementValue() > 180 || record.getMeasurementValue() < 90)) {
                alerts.add(new Alert(patient.getId(), "Critical Threshold Alert: Blood Pressure Out of Range", record.getTimestamp()));
            } else if (record.getRecordType().equals("DiastolicPressure") && (record.getMeasurementValue() > 120 || record.getMeasurementValue() < 60)) {
                alerts.add(new Alert(patient.getId(), "Critical Threshold Alert: Blood Pressure Out of Range", record.getTimestamp()));
            }
        }

        return alerts;
    }

    private boolean checkTrendConditions(List<Double> values) {
        if (values.size() < 3) {
            return false;
        }
        boolean increasingTrend = values.get(2) - values.get(1) > 10 && values.get(1) - values.get(0) > 10;
        boolean decreasingTrend = values.get(0) - values.get(1) > 10 && values.get(1) - values.get(2) > 10;
        return increasingTrend || decreasingTrend;
    }
}
