package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.StatementTypeAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.StatementType;
import by.victory.myapp.repository.StatementTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StatementTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatementTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/statement-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StatementTypeRepository statementTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatementTypeMockMvc;

    private StatementType statementType;

    private StatementType insertedStatementType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatementType createEntity() {
        return new StatementType().name(DEFAULT_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatementType createUpdatedEntity() {
        return new StatementType().name(UPDATED_NAME);
    }

    @BeforeEach
    public void initTest() {
        statementType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStatementType != null) {
            statementTypeRepository.delete(insertedStatementType);
            insertedStatementType = null;
        }
    }

    @Test
    @Transactional
    void createStatementType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StatementType
        var returnedStatementType = om.readValue(
            restStatementTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statementType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StatementType.class
        );

        // Validate the StatementType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStatementTypeUpdatableFieldsEquals(returnedStatementType, getPersistedStatementType(returnedStatementType));

        insertedStatementType = returnedStatementType;
    }

    @Test
    @Transactional
    void createStatementTypeWithExistingId() throws Exception {
        // Create the StatementType with an existing ID
        statementType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatementTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statementType)))
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statementType.setName(null);

        // Create the StatementType, which fails.

        restStatementTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statementType)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStatementTypes() throws Exception {
        // Initialize the database
        insertedStatementType = statementTypeRepository.saveAndFlush(statementType);

        // Get all the statementTypeList
        restStatementTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statementType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getStatementType() throws Exception {
        // Initialize the database
        insertedStatementType = statementTypeRepository.saveAndFlush(statementType);

        // Get the statementType
        restStatementTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, statementType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statementType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingStatementType() throws Exception {
        // Get the statementType
        restStatementTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatementType() throws Exception {
        // Initialize the database
        insertedStatementType = statementTypeRepository.saveAndFlush(statementType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statementType
        StatementType updatedStatementType = statementTypeRepository.findById(statementType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatementType are not directly saved in db
        em.detach(updatedStatementType);
        updatedStatementType.name(UPDATED_NAME);

        restStatementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatementType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStatementType))
            )
            .andExpect(status().isOk());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatementTypeToMatchAllProperties(updatedStatementType);
    }

    @Test
    @Transactional
    void putNonExistingStatementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statementType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statementType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statementType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatementTypeWithPatch() throws Exception {
        // Initialize the database
        insertedStatementType = statementTypeRepository.saveAndFlush(statementType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statementType using partial update
        StatementType partialUpdatedStatementType = new StatementType();
        partialUpdatedStatementType.setId(statementType.getId());

        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatementType))
            )
            .andExpect(status().isOk());

        // Validate the StatementType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatementTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStatementType, statementType),
            getPersistedStatementType(statementType)
        );
    }

    @Test
    @Transactional
    void fullUpdateStatementTypeWithPatch() throws Exception {
        // Initialize the database
        insertedStatementType = statementTypeRepository.saveAndFlush(statementType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statementType using partial update
        StatementType partialUpdatedStatementType = new StatementType();
        partialUpdatedStatementType.setId(statementType.getId());

        partialUpdatedStatementType.name(UPDATED_NAME);

        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatementType))
            )
            .andExpect(status().isOk());

        // Validate the StatementType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatementTypeUpdatableFieldsEquals(partialUpdatedStatementType, getPersistedStatementType(partialUpdatedStatementType));
    }

    @Test
    @Transactional
    void patchNonExistingStatementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statementType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(statementType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatementType() throws Exception {
        // Initialize the database
        insertedStatementType = statementTypeRepository.saveAndFlush(statementType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statementType
        restStatementTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, statementType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return statementTypeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected StatementType getPersistedStatementType(StatementType statementType) {
        return statementTypeRepository.findById(statementType.getId()).orElseThrow();
    }

    protected void assertPersistedStatementTypeToMatchAllProperties(StatementType expectedStatementType) {
        assertStatementTypeAllPropertiesEquals(expectedStatementType, getPersistedStatementType(expectedStatementType));
    }

    protected void assertPersistedStatementTypeToMatchUpdatableProperties(StatementType expectedStatementType) {
        assertStatementTypeAllUpdatablePropertiesEquals(expectedStatementType, getPersistedStatementType(expectedStatementType));
    }
}
