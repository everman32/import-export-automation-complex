package by.victory.myapp.domain;

import static by.victory.myapp.domain.PositioningTestSamples.*;
import static by.victory.myapp.domain.ProductTestSamples.*;
import static by.victory.myapp.domain.StatementTestSamples.*;
import static by.victory.myapp.domain.StatementTypeTestSamples.*;
import static by.victory.myapp.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Statement.class);
        Statement statement1 = getStatementSample1();
        Statement statement2 = new Statement();
        assertThat(statement1).isNotEqualTo(statement2);

        statement2.setId(statement1.getId());
        assertThat(statement1).isEqualTo(statement2);

        statement2 = getStatementSample2();
        assertThat(statement1).isNotEqualTo(statement2);
    }

    @Test
    void statementTypeTest() {
        Statement statement = getStatementRandomSampleGenerator();
        StatementType statementTypeBack = getStatementTypeRandomSampleGenerator();

        statement.setStatementType(statementTypeBack);
        assertThat(statement.getStatementType()).isEqualTo(statementTypeBack);

        statement.statementType(null);
        assertThat(statement.getStatementType()).isNull();
    }

    @Test
    void productTest() {
        Statement statement = getStatementRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        statement.setProduct(productBack);
        assertThat(statement.getProduct()).isEqualTo(productBack);

        statement.product(null);
        assertThat(statement.getProduct()).isNull();
    }

    @Test
    void positioningTest() {
        Statement statement = getStatementRandomSampleGenerator();
        Positioning positioningBack = getPositioningRandomSampleGenerator();

        statement.setPositioning(positioningBack);
        assertThat(statement.getPositioning()).isEqualTo(positioningBack);

        statement.positioning(null);
        assertThat(statement.getPositioning()).isNull();
    }

    @Test
    void tripTest() {
        Statement statement = getStatementRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        statement.setTrip(tripBack);
        assertThat(statement.getTrip()).isEqualTo(tripBack);

        statement.trip(null);
        assertThat(statement.getTrip()).isNull();
    }
}
