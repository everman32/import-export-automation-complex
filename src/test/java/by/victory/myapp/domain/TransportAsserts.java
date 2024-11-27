package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TransportAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransportAllPropertiesEquals(Transport expected, Transport actual) {
        assertTransportAutoGeneratedPropertiesEquals(expected, actual);
        assertTransportAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransportAllUpdatablePropertiesEquals(Transport expected, Transport actual) {
        assertTransportUpdatableFieldsEquals(expected, actual);
        assertTransportUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransportAutoGeneratedPropertiesEquals(Transport expected, Transport actual) {
        assertThat(expected)
            .as("Verify Transport auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransportUpdatableFieldsEquals(Transport expected, Transport actual) {
        assertThat(expected)
            .as("Verify Transport relevant properties")
            .satisfies(e -> assertThat(e.getBrand()).as("check brand").isEqualTo(actual.getBrand()))
            .satisfies(e -> assertThat(e.getModel()).as("check model").isEqualTo(actual.getModel()))
            .satisfies(e -> assertThat(e.getVin()).as("check vin").isEqualTo(actual.getVin()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTransportUpdatableRelationshipsEquals(Transport expected, Transport actual) {
        // empty method
    }
}
