package by.victory.myapp.domain;

import static by.victory.myapp.domain.DriverTestSamples.*;
import static by.victory.myapp.domain.ExportProdTestSamples.*;
import static by.victory.myapp.domain.ImportProdTestSamples.*;
import static by.victory.myapp.domain.PositioningTestSamples.*;
import static by.victory.myapp.domain.StatementTestSamples.*;
import static by.victory.myapp.domain.TransportTestSamples.*;
import static by.victory.myapp.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TripTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trip.class);
        Trip trip1 = getTripSample1();
        Trip trip2 = new Trip();
        assertThat(trip1).isNotEqualTo(trip2);

        trip2.setId(trip1.getId());
        assertThat(trip1).isEqualTo(trip2);

        trip2 = getTripSample2();
        assertThat(trip1).isNotEqualTo(trip2);
    }

    @Test
    void statementTest() {
        Trip trip = getTripRandomSampleGenerator();
        Statement statementBack = getStatementRandomSampleGenerator();

        trip.addStatement(statementBack);
        assertThat(trip.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getTrip()).isEqualTo(trip);

        trip.removeStatement(statementBack);
        assertThat(trip.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getTrip()).isNull();

        trip.statements(new HashSet<>(Set.of(statementBack)));
        assertThat(trip.getStatements()).containsOnly(statementBack);
        assertThat(statementBack.getTrip()).isEqualTo(trip);

        trip.setStatements(new HashSet<>());
        assertThat(trip.getStatements()).doesNotContain(statementBack);
        assertThat(statementBack.getTrip()).isNull();
    }

    @Test
    void importProdTest() {
        Trip trip = getTripRandomSampleGenerator();
        ImportProd importProdBack = getImportProdRandomSampleGenerator();

        trip.setImportProd(importProdBack);
        assertThat(trip.getImportProd()).isEqualTo(importProdBack);
        assertThat(importProdBack.getTrip()).isEqualTo(trip);

        trip.importProd(null);
        assertThat(trip.getImportProd()).isNull();
        assertThat(importProdBack.getTrip()).isNull();
    }

    @Test
    void exportProdTest() {
        Trip trip = getTripRandomSampleGenerator();
        ExportProd exportProdBack = getExportProdRandomSampleGenerator();

        trip.setExportProd(exportProdBack);
        assertThat(trip.getExportProd()).isEqualTo(exportProdBack);
        assertThat(exportProdBack.getTrip()).isEqualTo(trip);

        trip.exportProd(null);
        assertThat(trip.getExportProd()).isNull();
        assertThat(exportProdBack.getTrip()).isNull();
    }

    @Test
    void transportTest() {
        Trip trip = getTripRandomSampleGenerator();
        Transport transportBack = getTransportRandomSampleGenerator();

        trip.setTransport(transportBack);
        assertThat(trip.getTransport()).isEqualTo(transportBack);

        trip.transport(null);
        assertThat(trip.getTransport()).isNull();
    }

    @Test
    void driverTest() {
        Trip trip = getTripRandomSampleGenerator();
        Driver driverBack = getDriverRandomSampleGenerator();

        trip.setDriver(driverBack);
        assertThat(trip.getDriver()).isEqualTo(driverBack);

        trip.driver(null);
        assertThat(trip.getDriver()).isNull();
    }

    @Test
    void hubPositioningTest() {
        Trip trip = getTripRandomSampleGenerator();
        Positioning positioningBack = getPositioningRandomSampleGenerator();

        trip.setHubPositioning(positioningBack);
        assertThat(trip.getHubPositioning()).isEqualTo(positioningBack);

        trip.hubPositioning(null);
        assertThat(trip.getHubPositioning()).isNull();
    }
}
