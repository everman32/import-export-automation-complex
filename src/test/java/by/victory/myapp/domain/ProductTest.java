package by.victory.myapp.domain;

import static by.victory.myapp.domain.ProductTestSamples.*;
import static by.victory.myapp.domain.ProductUnitTestSamples.*;
import static by.victory.myapp.domain.StatementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void statementTest() {
        Product product = getProductRandomSampleGenerator();
        Statement statementBack = getStatementRandomSampleGenerator();

        product.addStatement(statementBack);
        assertThat(product.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getProduct()).isEqualTo(product);

        product.removeStatement(statementBack);
        assertThat(product.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getProduct()).isNull();

        product.statements(new HashSet<>(Set.of(statementBack)));
        assertThat(product.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getProduct()).isEqualTo(product);

        product.setStatements(new HashSet<>());
        assertThat(product.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getProduct()).isNull();
    }

    @Test
    void productUnitTest() {
        Product product = getProductRandomSampleGenerator();
        ProductUnit productUnitBack = getProductUnitRandomSampleGenerator();

        product.setProductUnit(productUnitBack);
        assertThat(product.getProductUnit()).isEqualTo(productUnitBack);

        product.productUnit(null);
        assertThat(product.getProductUnit()).isNull();
    }
}
