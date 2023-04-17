package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatementTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatementType.class);
        StatementType statementType1 = new StatementType();
        statementType1.setId(1L);
        StatementType statementType2 = new StatementType();
        statementType2.setId(statementType1.getId());
        assertThat(statementType1).isEqualTo(statementType2);
        statementType2.setId(2L);
        assertThat(statementType1).isNotEqualTo(statementType2);
        statementType1.setId(null);
        assertThat(statementType1).isNotEqualTo(statementType2);
    }
}
