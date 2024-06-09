package com.alerts;

import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts;
    private List<AlertStrategy> strategies;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
        this.strategies = new ArrayList<>();
        strategies.add(new BloodPressureStrategy());
        strategies.add(new HeartRateStrategy());
        strategies.add(new OxygenSaturationStrategy());
    }

    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : strategies) {
            List<Alert> strategyAlerts = strategy.checkAlert(patient);
            for (Alert alert : strategyAlerts) {
                Alert repeatedAlert = new RepeatedAlertDecorator(alert, 60000); // Recheck every 60 seconds
                Alert priorityAlert = new PriorityAlertDecorator(repeatedAlert, "High");
                alerts.add(priorityAlert);
            }
        }
    }

    public int getAlertCount() {
        return alerts.size();
    }

    public List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    public List<Alert> getAlertsByPatientId(String patientId) {
        return alerts.stream()
                .filter(alert -> alert.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }
}
