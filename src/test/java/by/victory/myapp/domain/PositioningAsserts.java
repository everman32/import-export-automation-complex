package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PositioningAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPositioningAllPropertiesEquals(Positioning expected, Positioning actual) {
        assertPositioningAutoGeneratedPropertiesEquals(expected, actual);
        assertPositioningAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPositioningAllUpdatablePropertiesEquals(Positioning expected, Positioning actual) {
        assertPositioningUpdatableFieldsEquals(expected, actual);
        assertPositioningUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPositioningAutoGeneratedPropertiesEquals(Positioning expected, Positioning actual) {
        assertThat(expected)
            .as("Verify Positioning auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPositioningUpdatableFieldsEquals(Positioning expected, Positioning actual) {
        assertThat(expected)
            .as("Verify Positioning relevant properties")
            .satisfies(e -> assertThat(e.getLatitude()).as("check latitude").isEqualTo(actual.getLatitude()))
            .satisfies(e -> assertThat(e.getLongitude()).as("check longitude").isEqualTo(actual.getLongitude()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPositioningUpdatableRelationshipsEquals(Positioning expected, Positioning actual) {
        // empty method
    }
}
