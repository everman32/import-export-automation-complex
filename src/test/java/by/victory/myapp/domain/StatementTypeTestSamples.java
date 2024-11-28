package by.victory.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StatementTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StatementType getStatementTypeSample1() {
        return new StatementType().id(1L).name("name1");
    }

    public static StatementType getStatementTypeSample2() {
        return new StatementType().id(2L).name("name2");
    }

    public static StatementType getStatementTypeRandomSampleGenerator() {
        return new StatementType().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
