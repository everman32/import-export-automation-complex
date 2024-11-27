package by.victory.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductUnitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductUnit getProductUnitSample1() {
        return new ProductUnit().id(1L).name("name1").description("description1");
    }

    public static ProductUnit getProductUnitSample2() {
        return new ProductUnit().id(2L).name("name2").description("description2");
    }

    public static ProductUnit getProductUnitRandomSampleGenerator() {
        return new ProductUnit()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
