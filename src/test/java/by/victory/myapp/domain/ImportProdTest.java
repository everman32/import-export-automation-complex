package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ImportProdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImportProd.class);
        ImportProd importProd1 = new ImportProd();
        importProd1.setId(1L);
        ImportProd importProd2 = new ImportProd();
        importProd2.setId(importProd1.getId());
        assertThat(importProd1).isEqualTo(importProd2);
        importProd2.setId(2L);
        assertThat(importProd1).isNotEqualTo(importProd2);
        importProd1.setId(null);
        assertThat(importProd1).isNotEqualTo(importProd2);
    }
}
