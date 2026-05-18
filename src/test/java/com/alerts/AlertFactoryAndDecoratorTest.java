package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;

class AlertFactoryAndDecoratorTest {

    @Test
    void factoriesCreateCorrectAlert() {
        AlertFactory bp = new BloodPressureAlertFactory();
        AlertFactory ox = new BloodOxygenAlertFactory();
        AlertFactory ecg = new ECGAlertFactory();

        Alert a1 = bp.createAlert("1", "BP_HIGH", 1000L);
        Alert a2 = ox.createAlert("2", "LOW_SAT", 2000L);
        Alert a3 = ecg.createAlert("3", "ECG_PEAK", 3000L);

        assertEquals("1", a1.getPatientId());
        assertEquals("BP_HIGH", a1.getCondition());
        assertEquals(1000L, a1.getTimestamp());

        assertEquals("2", a2.getPatientId());
        assertEquals("LOW_SAT", a2.getCondition());

        assertEquals("3", a3.getPatientId());
        assertEquals(3000L, a3.getTimestamp());
    }

    @Test
    void decoratorAddsPriorityAndPreservesData() {
        Alert base = new BasicAlert("7", "TEST", 123L);
        PriorityAlertDecorator p = new PriorityAlertDecorator(base, "URGENT");
        assertEquals("7", p.getPatientId());
        assertEquals("[URGENT] TEST", p.getCondition());
        assertEquals(123L, p.getTimestamp());
    }

    @Test
    void singletonDataStorageReturnsSameInstance() {
        DataStorage a = DataStorage.getInstance();
        DataStorage b = DataStorage.getInstance();
        assertSame(a, b);
    }
}
