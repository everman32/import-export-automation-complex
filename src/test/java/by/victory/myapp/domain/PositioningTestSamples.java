package by.victory.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PositioningTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Positioning getPositioningSample1() {
        return new Positioning().id(1L);
    }

    public static Positioning getPositioningSample2() {
        return new Positioning().id(2L);
    }

    public static Positioning getPositioningRandomSampleGenerator() {
        return new Positioning().id(longCount.incrementAndGet());
    }
}
