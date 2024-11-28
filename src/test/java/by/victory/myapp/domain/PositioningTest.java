package by.victory.myapp.domain;

import static by.victory.myapp.domain.PositioningTestSamples.*;
import static by.victory.myapp.domain.StatementTestSamples.*;
import static by.victory.myapp.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PositioningTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Positioning.class);
        Positioning positioning1 = getPositioningSample1();
        Positioning positioning2 = new Positioning();
        assertThat(positioning1).isNotEqualTo(positioning2);

        positioning2.setId(positioning1.getId());
        assertThat(positioning1).isEqualTo(positioning2);

        positioning2 = getPositioningSample2();
        assertThat(positioning1).isNotEqualTo(positioning2);
    }

    @Test
    void statementTest() {
        Positioning positioning = getPositioningRandomSampleGenerator();
        Statement statementBack = getStatementRandomSampleGenerator();

        positioning.addStatement(statementBack);
        assertThat(positioning.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getPositioning()).isEqualTo(positioning);

        positioning.removeStatement(statementBack);
        assertThat(positioning.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getPositioning()).isNull();

        positioning.statements(new HashSet<>(Set.of(statementBack)));
        assertThat(positioning.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getPositioning()).isEqualTo(positioning);

        positioning.setStatements(new HashSet<>());
        assertThat(positioning.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getPositioning()).isNull();
    }

    @Test
    void tripTest() {
        Positioning positioning = getPositioningRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        positioning.addTrip(tripBack);
        assertThat(positioning.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getHubPositioning()).isEqualTo(positioning);

        positioning.removeTrip(tripBack);
        assertThat(positioning.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getHubPositioning()).isNull();

        positioning.trips(new HashSet<>(Set.of(tripBack)));
        assertThat(positioning.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getHubPositioning()).isEqualTo(positioning);

        positioning.setTrips(new HashSet<>());
        assertThat(positioning.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getHubPositioning()).isNull();
    }
}
