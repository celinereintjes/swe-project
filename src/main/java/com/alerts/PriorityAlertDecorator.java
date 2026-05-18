package com.alerts;

/**
 * Adds a priority tag to an alert.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    private final String priorityTag;

    public PriorityAlertDecorator(Alert wrapped, String priorityTag) {
        super(wrapped);
        this.priorityTag = priorityTag;
    }

    @Override
    public String getCondition() {
        return String.format("[%s] %s", priorityTag, super.getCondition());
    }
}
