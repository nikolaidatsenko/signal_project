package com.alerts.decorators;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator {
    private String priority;

    public PriorityAlertDecorator(Alert decoratedAlert, String priority) {
        super(decoratedAlert);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return super.getCondition() + " [Priority: " + priority + "]";
    }
}
