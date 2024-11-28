package by.victory.myapp.domain;

import static by.victory.myapp.domain.TransportTestSamples.*;
import static by.victory.myapp.domain.TripTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TransportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transport.class);
        Transport transport1 = getTransportSample1();
        Transport transport2 = new Transport();
        assertThat(transport1).isNotEqualTo(transport2);

        transport2.setId(transport1.getId());
        assertThat(transport1).isEqualTo(transport2);

        transport2 = getTransportSample2();
        assertThat(transport1).isNotEqualTo(transport2);
    }

    @Test
    void tripTest() {
        Transport transport = getTransportRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        transport.addTrip(tripBack);
        assertThat(transport.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getTransport()).isEqualTo(transport);

        transport.removeTrip(tripBack);
        assertThat(transport.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getTransport()).isNull();

        transport.trips(new HashSet<>(Set.of(tripBack)));
        assertThat(transport.getTrips()).containsOnly(tripBack);
        assertThat(tripBack.getTransport()).isEqualTo(transport);

        transport.setTrips(new HashSet<>());
        assertThat(transport.getTrips()).doesNotContain(tripBack);
        assertThat(tripBack.getTransport()).isNull();
    }
}
