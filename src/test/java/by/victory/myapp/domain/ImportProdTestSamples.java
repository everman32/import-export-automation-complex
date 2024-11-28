package by.victory.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ImportProdTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ImportProd getImportProdSample1() {
        return new ImportProd().id(1L);
    }

    public static ImportProd getImportProdSample2() {
        return new ImportProd().id(2L);
    }

    public static ImportProd getImportProdRandomSampleGenerator() {
        return new ImportProd().id(longCount.incrementAndGet());
    }
}
