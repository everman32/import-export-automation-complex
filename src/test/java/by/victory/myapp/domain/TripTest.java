package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TripTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trip.class);
        Trip trip1 = new Trip();
        trip1.setId(1L);
        Trip trip2 = new Trip();
        trip2.setId(trip1.getId());
        assertThat(trip1).isEqualTo(trip2);
        trip2.setId(2L);
        assertThat(trip1).isNotEqualTo(trip2);
        trip1.setId(null);
        assertThat(trip1).isNotEqualTo(trip2);
    }
}
