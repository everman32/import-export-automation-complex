package by.victory.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TripTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Trip getTripSample1() {
        return new Trip().id(1L);
    }

    public static Trip getTripSample2() {
        return new Trip().id(2L);
    }

    public static Trip getTripRandomSampleGenerator() {
        return new Trip().id(longCount.incrementAndGet());
    }
}
