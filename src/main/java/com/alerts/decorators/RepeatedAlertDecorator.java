package com.alerts.decorators;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator {
    private long interval;
    private long lastChecked;

    public RepeatedAlertDecorator(Alert decoratedAlert, long interval) {
        super(decoratedAlert);
        this.interval = interval;
        this.lastChecked = decoratedAlert.getTimestamp();
    }

    @Override
    public String getCondition() {
        if (System.currentTimeMillis() - lastChecked > interval) {
            lastChecked = System.currentTimeMillis();
            return super.getCondition() + " (rechecked)";
        }
        return super.getCondition();
    }

    // Method to manually set the last checked time for testing purposes
    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }
}
