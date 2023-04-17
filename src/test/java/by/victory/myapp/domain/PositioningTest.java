package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import by.victory.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PositioningTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Positioning.class);
        Positioning positioning1 = new Positioning();
        positioning1.setId(1L);
        Positioning positioning2 = new Positioning();
        positioning2.setId(positioning1.getId());
        assertThat(positioning1).isEqualTo(positioning2);
        positioning2.setId(2L);
        assertThat(positioning1).isNotEqualTo(positioning2);
        positioning1.setId(null);
        assertThat(positioning1).isNotEqualTo(positioning2);
    }
}
