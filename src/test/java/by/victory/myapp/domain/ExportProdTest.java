package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExportProdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExportProd.class);
        ExportProd exportProd1 = new ExportProd();
        exportProd1.setId(1L);
        ExportProd exportProd2 = new ExportProd();
        exportProd2.setId(exportProd1.getId());
        assertThat(exportProd1).isEqualTo(exportProd2);
        exportProd2.setId(2L);
        assertThat(exportProd1).isNotEqualTo(exportProd2);
        exportProd1.setId(null);
        assertThat(exportProd1).isNotEqualTo(exportProd2);
    }
}
