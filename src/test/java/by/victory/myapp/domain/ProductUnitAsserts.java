package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductUnitAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductUnitAllPropertiesEquals(ProductUnit expected, ProductUnit actual) {
        assertProductUnitAutoGeneratedPropertiesEquals(expected, actual);
        assertProductUnitAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductUnitAllUpdatablePropertiesEquals(ProductUnit expected, ProductUnit actual) {
        assertProductUnitUpdatableFieldsEquals(expected, actual);
        assertProductUnitUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductUnitAutoGeneratedPropertiesEquals(ProductUnit expected, ProductUnit actual) {
        assertThat(expected)
            .as("Verify ProductUnit auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductUnitUpdatableFieldsEquals(ProductUnit expected, ProductUnit actual) {
        assertThat(expected)
            .as("Verify ProductUnit relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProductUnitUpdatableRelationshipsEquals(ProductUnit expected, ProductUnit actual) {
        // empty method
    }
}