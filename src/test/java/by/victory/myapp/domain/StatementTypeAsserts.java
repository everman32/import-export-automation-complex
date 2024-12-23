package by.victory.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class StatementTypeAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStatementTypeAllPropertiesEquals(StatementType expected, StatementType actual) {
        assertStatementTypeAutoGeneratedPropertiesEquals(expected, actual);
        assertStatementTypeAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStatementTypeAllUpdatablePropertiesEquals(StatementType expected, StatementType actual) {
        assertStatementTypeUpdatableFieldsEquals(expected, actual);
        assertStatementTypeUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStatementTypeAutoGeneratedPropertiesEquals(StatementType expected, StatementType actual) {
        assertThat(expected)
            .as("Verify StatementType auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStatementTypeUpdatableFieldsEquals(StatementType expected, StatementType actual) {
        assertThat(expected)
            .as("Verify StatementType relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertStatementTypeUpdatableRelationshipsEquals(StatementType expected, StatementType actual) {
        // empty method
    }
}
