package by.victory.myapp.domain;

import static by.victory.myapp.domain.ExportProdTestSamples.*;
import static by.victory.myapp.domain.GradeTestSamples.*;
import static by.victory.myapp.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExportProdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExportProd.class);
        ExportProd exportProd1 = getExportProdSample1();
        ExportProd exportProd2 = new ExportProd();
        assertThat(exportProd1).isNotEqualTo(exportProd2);

        exportProd2.setId(exportProd1.getId());
        assertThat(exportProd1).isEqualTo(exportProd2);

        exportProd2 = getExportProdSample2();
        assertThat(exportProd1).isNotEqualTo(exportProd2);
    }

    @Test
    void tripTest() {
        ExportProd exportProd = getExportProdRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        exportProd.setTrip(tripBack);
        assertThat(exportProd.getTrip()).isEqualTo(tripBack);

        exportProd.trip(null);
        assertThat(exportProd.getTrip()).isNull();
    }

    @Test
    void gradeTest() {
        ExportProd exportProd = getExportProdRandomSampleGenerator();
        Grade gradeBack = getGradeRandomSampleGenerator();

        exportProd.setGrade(gradeBack);
        assertThat(exportProd.getGrade()).isEqualTo(gradeBack);

        exportProd.grade(null);
        assertThat(exportProd.getGrade()).isNull();
    }
}
