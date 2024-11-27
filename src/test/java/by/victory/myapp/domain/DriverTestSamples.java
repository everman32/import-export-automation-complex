package by.victory.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DriverTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Driver getDriverSample1() {
        return new Driver().id(1L).firstname("firstname1").patronymic("patronymic1").lastname("lastname1").phone("phone1");
    }

    public static Driver getDriverSample2() {
        return new Driver().id(2L).firstname("firstname2").patronymic("patronymic2").lastname("lastname2").phone("phone2");
    }

    public static Driver getDriverRandomSampleGenerator() {
        return new Driver()
            .id(longCount.incrementAndGet())
            .firstname(UUID.randomUUID().toString())
            .patronymic(UUID.randomUUID().toString())
            .lastname(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
