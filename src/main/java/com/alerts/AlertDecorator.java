package com.alerts;

/**
 * Base decorator for Alert. Delegates to wrapped Alert.
 */
public abstract class AlertDecorator implements Alert {
    protected final Alert wrapped;

    protected AlertDecorator(Alert wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getPatientId() {
        return wrapped.getPatientId();
    }

    @Override
    public String getCondition() {
        return wrapped.getCondition();
    }

    @Override
    public long getTimestamp() {
        return wrapped.getTimestamp();
    }
}
