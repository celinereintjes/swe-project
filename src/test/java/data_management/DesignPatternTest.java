package data_management;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BasicAlert;
import com.alerts.BloodOxygenAlertFactory;
import com.alerts.BloodPressureAlertFactory;
import com.alerts.ECGAlertFactory;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

public class DesignPatternTest {
    @Test
    void testBloodPressureAlertFactory() {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "High Systolic", 1000L);
        assertNotNull(alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("High Systolic", alert.getCondition());
    }

    @Test
    void testBloodOxygenAlertFactory() {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Low Saturation", 2000L);
        assertNotNull(alert);
        assertEquals("Low Saturation", alert.getCondition());
    }

    @Test
    void testECGAlertFactory() {
        ECGAlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "Abnormal Peak", 3000L);
        assertNotNull(alert);
        assertEquals("Abnormal Peak", alert.getCondition());
    }

    @Test
    void testPriorityAlertDecorator() {
        Alert base = new BasicAlert("1", "Low Saturation", 1000L);
        PriorityAlertDecorator decorated = new PriorityAlertDecorator(base, "HIGH PRIORITY");
        assertTrue(decorated.getCondition().contains("[HIGH PRIORITY]"));
        assertEquals("1", decorated.getPatientId());
    }

    @Test
    void testRepeatedAlertDecorator() {
        Alert base = new BasicAlert("2", "High BP", 2000L);
        RepeatedAlertDecorator decorated = new RepeatedAlertDecorator(base, 3);
        assertTrue(decorated.getCondition().contains("High BP"));
    }

    @Test
    void testDataStorageSingleton() {
        DataStorage a = DataStorage.getInstance();
        DataStorage b = DataStorage.getInstance();
        assertSame(a, b);
    }

    @Test
    void testHealthDataSimulatorSingleton() {
        HealthDataSimulator a = HealthDataSimulator.getInstance();
        HealthDataSimulator b = HealthDataSimulator.getInstance();
        assertSame(a, b);
    }
}
