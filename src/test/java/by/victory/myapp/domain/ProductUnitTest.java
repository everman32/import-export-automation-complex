package by.victory.myapp.domain;

import static by.victory.myapp.domain.ProductTestSamples.*;
import static by.victory.myapp.domain.ProductUnitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductUnitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductUnit.class);
        ProductUnit productUnit1 = getProductUnitSample1();
        ProductUnit productUnit2 = new ProductUnit();
        assertThat(productUnit1).isNotEqualTo(productUnit2);

        productUnit2.setId(productUnit1.getId());
        assertThat(productUnit1).isEqualTo(productUnit2);

        productUnit2 = getProductUnitSample2();
        assertThat(productUnit1).isNotEqualTo(productUnit2);
    }

    @Test
    void productTest() {
        ProductUnit productUnit = getProductUnitRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productUnit.addProduct(productBack);
        assertThat(productUnit.getProducts()).containsOnly(productBack);
        assertThat(productBack.getProductUnit()).isEqualTo(productUnit);

        productUnit.removeProduct(productBack);
        assertThat(productUnit.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getProductUnit()).isNull();

        productUnit.products(new HashSet<>(Set.of(productBack)));
        assertThat(productUnit.getProducts()).containsOnly(productBack);
        assertThat(productBack.getProductUnit()).isEqualTo(productUnit);

        productUnit.setProducts(new HashSet<>());
        assertThat(productUnit.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getProductUnit()).isNull();
    }
}
