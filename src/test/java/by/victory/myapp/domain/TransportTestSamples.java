package by.victory.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Transport getTransportSample1() {
        return new Transport().id(1L).brand("brand1").model("model1").vin("vin1");
    }

    public static Transport getTransportSample2() {
        return new Transport().id(2L).brand("brand2").model("model2").vin("vin2");
    }

    public static Transport getTransportRandomSampleGenerator() {
        return new Transport()
            .id(longCount.incrementAndGet())
            .brand(UUID.randomUUID().toString())
            .model(UUID.randomUUID().toString())
            .vin(UUID.randomUUID().toString());
    }
}
