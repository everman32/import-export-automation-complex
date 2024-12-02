package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportProdAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertImportProdAllPropertiesEquals(ImportProd expected, ImportProd actual) {
        assertImportProdAutoGeneratedPropertiesEquals(expected, actual);
        assertImportProdAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertImportProdAllUpdatablePropertiesEquals(ImportProd expected, ImportProd actual) {
        assertImportProdUpdatableFieldsEquals(expected, actual);
        assertImportProdUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertImportProdAutoGeneratedPropertiesEquals(ImportProd expected, ImportProd actual) {
        assertThat(expected)
            .as("Verify ImportProd auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertImportProdUpdatableFieldsEquals(ImportProd expected, ImportProd actual) {
        assertThat(expected)
            .as("Verify ImportProd relevant properties")
            .satisfies(e -> assertThat(e.getArrivalDate()).as("check arrivalDate").isEqualTo(actual.getArrivalDate()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertImportProdUpdatableRelationshipsEquals(ImportProd expected, ImportProd actual) {
        assertThat(expected)
            .as("Verify ImportProd relationships")
            .satisfies(e -> assertThat(e.getTrip()).as("check trip").isEqualTo(actual.getTrip()))
            .satisfies(e -> assertThat(e.getGrade()).as("check grade").isEqualTo(actual.getGrade()));
    }
}