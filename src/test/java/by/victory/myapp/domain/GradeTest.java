package by.victory.myapp.domain;

import static by.victory.myapp.domain.ExportProdTestSamples.*;
import static by.victory.myapp.domain.GradeTestSamples.*;
import static by.victory.myapp.domain.ImportProdTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GradeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Grade.class);
        Grade grade1 = getGradeSample1();
        Grade grade2 = new Grade();
        assertThat(grade1).isNotEqualTo(grade2);

        grade2.setId(grade1.getId());
        assertThat(grade1).isEqualTo(grade2);

        grade2 = getGradeSample2();
        assertThat(grade1).isNotEqualTo(grade2);
    }

    @Test
    void importProdTest() {
        Grade grade = getGradeRandomSampleGenerator();
        ImportProd importProdBack = getImportProdRandomSampleGenerator();

        grade.addImportProd(importProdBack);
        assertThat(grade.getImportProds()).containsOnly(importProdBack);
        assertThat(importProdBack.getGrade()).isEqualTo(grade);

        grade.removeImportProd(importProdBack);
        assertThat(grade.getImportProds()).doesNotContain(importProdBack);
        assertThat(importProdBack.getGrade()).isNull();

        grade.importProds(new HashSet<>(Set.of(importProdBack)));
        assertThat(grade.getImportProds()).containsOnly(importProdBack);
        assertThat(importProdBack.getGrade()).isEqualTo(grade);

        grade.setImportProds(new HashSet<>());
        assertThat(grade.getImportProds()).doesNotContain(importProdBack);
        assertThat(importProdBack.getGrade()).isNull();
    }

    @Test
    void exportProdTest() {
        Grade grade = getGradeRandomSampleGenerator();
        ExportProd exportProdBack = getExportProdRandomSampleGenerator();

        grade.addExportProd(exportProdBack);
        assertThat(grade.getExportProds()).containsOnly(exportProdBack);
        assertThat(exportProdBack.getGrade()).isEqualTo(grade);

        grade.removeExportProd(exportProdBack);
        assertThat(grade.getExportProds()).doesNotContain(exportProdBack);
        assertThat(exportProdBack.getGrade()).isNull();

        grade.exportProds(new HashSet<>(Set.of(exportProdBack)));
        assertThat(grade.getExportProds()).containsOnly(exportProdBack);
        assertThat(exportProdBack.getGrade()).isEqualTo(grade);

        grade.setExportProds(new HashSet<>());
        assertThat(grade.getExportProds()).doesNotContain(exportProdBack);
        assertThat(exportProdBack.getGrade()).isNull();
    }
}
