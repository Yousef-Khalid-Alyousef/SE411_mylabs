package edu.psu.se411.lab08;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SensorTest {
    @Test
    void notifiesBothObserversOnlyWhenReadingChanges() {
        TemperatureSensor sensor = new TemperatureSensor("Temperature");
        AtomicInteger dashboardCalls = new AtomicInteger();
        AtomicInteger loggerCalls = new AtomicInteger();
        sensor.register(subject -> {
            assertSame(sensor, subject);
            assertEquals(25, ((Sensor) subject).getReading());
            dashboardCalls.incrementAndGet();
        });
        sensor.register(subject -> loggerCalls.incrementAndGet());
        sensor.setReading(25);
        sensor.setReading(25);
        assertEquals(1, dashboardCalls.get());
        assertEquals(1, loggerCalls.get());
    }

    @Test
    void duplicateRegistrationAndUnregistrationWork() {
        HumiditySensor sensor = new HumiditySensor("Humidity");
        AtomicInteger calls = new AtomicInteger();
        Observer observer = subject -> calls.incrementAndGet();
        sensor.register(observer);
        sensor.register(observer);
        sensor.setReading(50);
        assertEquals(1, calls.get());
        sensor.unregister(observer);
        sensor.setReading(60);
        assertEquals(1, calls.get());
        assertThrows(NullPointerException.class, () -> sensor.register(null));
    }

    @Test
    void bothKindsOfCloneHaveIndependentReadingsAndObserverLists() {
        for (Sensor original : new Sensor[] {
                new TemperatureSensor("Temperature"), new HumiditySensor("Humidity")}) {
            original.setReading(20);
            AtomicInteger originalCalls = new AtomicInteger();
            AtomicInteger cloneCalls = new AtomicInteger();
            Observer originalObserver = subject -> originalCalls.incrementAndGet();
            original.register(originalObserver);
            Sensor clone = original.clone();
            assertNotSame(original, clone);
            assertEquals(original.getClass(), clone.getClass());
            assertEquals(20, clone.getReading());
            clone.setReading(30);
            assertEquals(0, originalCalls.get());
            assertEquals(20, original.getReading());
            clone.register(subject -> cloneCalls.incrementAndGet());
            clone.setReading(31);
            assertEquals(1, cloneCalls.get());
            assertEquals(0, originalCalls.get());
            clone.unregister(originalObserver);
            original.setReading(40);
            assertEquals(1, originalCalls.get());
            assertEquals(1, cloneCalls.get());
            original.unregister(originalObserver);
            clone.setReading(32);
            assertEquals(2, cloneCalls.get());
        }
    }

    @Test
    void observerCanUnregisterItselfDuringNotification() {
        Sensor sensor = new TemperatureSensor("Temperature");
        AtomicInteger calls = new AtomicInteger();
        Observer selfRemoving = new Observer() {
            @Override
            public void update(Subject subject) {
                calls.incrementAndGet();
                subject.unregister(this);
            }
        };
        sensor.register(selfRemoving);
        sensor.setReading(1);
        sensor.setReading(2);
        assertEquals(1, calls.get());
    }

    @Test
    void rejectsInvalidReadingsWithoutChangingStateOrNotifying() {
        HumiditySensor sensor = new HumiditySensor("Humidity");
        sensor.setReading(50);
        AtomicInteger calls = new AtomicInteger();
        sensor.register(subject -> calls.incrementAndGet());
        for (double invalid : new double[] {-1, 101, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> sensor.setReading(invalid));
        }
        assertEquals(50, sensor.getReading());
        assertEquals(0, calls.get());
        assertDoesNotThrow(() -> sensor.setReading(0));
        assertDoesNotThrow(() -> sensor.setReading(100));
        assertEquals(2, calls.get());
    }
}
