package by.victory.myapp.domain;

import static by.victory.myapp.domain.StatementTestSamples.*;
import static by.victory.myapp.domain.StatementTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StatementTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatementType.class);
        StatementType statementType1 = getStatementTypeSample1();
        StatementType statementType2 = new StatementType();
        assertThat(statementType1).isNotEqualTo(statementType2);

        statementType2.setId(statementType1.getId());
        assertThat(statementType1).isEqualTo(statementType2);

        statementType2 = getStatementTypeSample2();
        assertThat(statementType1).isNotEqualTo(statementType2);
    }

    @Test
    void statementTest() {
        StatementType statementType = getStatementTypeRandomSampleGenerator();
        Statement statementBack = getStatementRandomSampleGenerator();

        statementType.addStatement(statementBack);
        assertThat(statementType.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getStatementType()).isEqualTo(statementType);

        statementType.removeStatement(statementBack);
        assertThat(statementType.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getStatementType()).isNull();

        statementType.statements(new HashSet<>(Set.of(statementBack)));
        assertThat(statementType.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getStatementType()).isEqualTo(statementType);

        statementType.setStatements(new HashSet<>());
        assertThat(statementType.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getStatementType()).isNull();
    }
}
