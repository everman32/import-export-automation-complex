package by.victory.myapp.domain;

import static by.victory.myapp.domain.GradeTestSamples.*;
import static by.victory.myapp.domain.ImportProdTestSamples.*;
import static by.victory.myapp.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportProdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportProd.class);
        ImportProd importProd1 = getImportProdSample1();
        ImportProd importProd2 = new ImportProd();
        assertThat(importProd1).isNotEqualTo(importProd2);

        importProd2.setId(importProd1.getId());
        assertThat(importProd1).isEqualTo(importProd2);

        importProd2 = getImportProdSample2();
        assertThat(importProd1).isNotEqualTo(importProd2);
    }

    @Test
    void tripTest() {
        ImportProd importProd = getImportProdRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        importProd.setTrip(tripBack);
        assertThat(importProd.getTrip()).isEqualTo(tripBack);

        importProd.trip(null);
        assertThat(importProd.getTrip()).isNull();
    }

    @Test
    void gradeTest() {
        ImportProd importProd = getImportProdRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        importProd.setGrade(gradeBack);
        assertThat(importProd.getGrade()).isEqualTo(gradeBack);

        importProd.grade(null);
        assertThat(importProd.getGrade()).isNull();
    }
}
