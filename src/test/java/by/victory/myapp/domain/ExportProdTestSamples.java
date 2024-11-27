package by.victory.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ExportProdTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ExportProd getExportProdSample1() {
        return new ExportProd().id(1L);
    }

    public static ExportProd getExportProdSample2() {
        return new ExportProd().id(2L);
    }

    public static ExportProd getExportProdRandomSampleGenerator() {
        return new ExportProd().id(longCount.incrementAndGet());
    }
}
