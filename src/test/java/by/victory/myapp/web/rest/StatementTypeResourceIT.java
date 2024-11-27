package by.victory.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.StatementType;
import by.victory.myapp.repository.StatementTypeRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
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
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatementTypeRepository statementTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatementTypeMockMvc;

    private StatementType statementType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatementType createEntity(EntityManager em) {
        StatementType statementType = new StatementType().name(DEFAULT_NAME);
        return statementType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatementType createUpdatedEntity(EntityManager em) {
        StatementType statementType = new StatementType().name(UPDATED_NAME);
        return statementType;
    }

    @BeforeEach
    public void initTest() {
        statementType = createEntity(em);
    }

    @Test
    @Transactional
    void createStatementType() throws Exception {
        int databaseSizeBeforeCreate = statementTypeRepository.findAll().size();
        // Create the StatementType
        restStatementTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statementType)))
            .andExpect(status().isCreated());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeCreate + 1);
        StatementType testStatementType = statementTypeList.get(statementTypeList.size() - 1);
        assertThat(testStatementType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createStatementTypeWithExistingId() throws Exception {
        // Create the StatementType with an existing ID
        statementType.setId(1L);

        int databaseSizeBeforeCreate = statementTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatementTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statementType)))
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = statementTypeRepository.findAll().size();
        // set the field null
        statementType.setName(null);

        // Create the StatementType, which fails.

        restStatementTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statementType)))
            .andExpect(status().isBadRequest());

        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStatementTypes() throws Exception {
        // Initialize the database
        statementTypeRepository.saveAndFlush(statementType);

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
        statementTypeRepository.saveAndFlush(statementType);

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
    void putNewStatementType() throws Exception {
        // Initialize the database
        statementTypeRepository.saveAndFlush(statementType);

        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();

        // Update the statementType
        StatementType updatedStatementType = statementTypeRepository.findById(statementType.getId()).get();
        // Disconnect from session so that the updates on updatedStatementType are not directly saved in db
        em.detach(updatedStatementType);
        updatedStatementType.name(UPDATED_NAME);

        restStatementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatementType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStatementType))
            )
            .andExpect(status().isOk());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
        StatementType testStatementType = statementTypeList.get(statementTypeList.size() - 1);
        assertThat(testStatementType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingStatementType() throws Exception {
        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();
        statementType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statementType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatementType() throws Exception {
        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();
        statementType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatementType() throws Exception {
        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();
        statementType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statementType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatementTypeWithPatch() throws Exception {
        // Initialize the database
        statementTypeRepository.saveAndFlush(statementType);

        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();

        // Update the statementType using partial update
        StatementType partialUpdatedStatementType = new StatementType();
        partialUpdatedStatementType.setId(statementType.getId());

        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatementType))
            )
            .andExpect(status().isOk());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
        StatementType testStatementType = statementTypeList.get(statementTypeList.size() - 1);
        assertThat(testStatementType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateStatementTypeWithPatch() throws Exception {
        // Initialize the database
        statementTypeRepository.saveAndFlush(statementType);

        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();

        // Update the statementType using partial update
        StatementType partialUpdatedStatementType = new StatementType();
        partialUpdatedStatementType.setId(statementType.getId());

        partialUpdatedStatementType.name(UPDATED_NAME);

        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatementType))
            )
            .andExpect(status().isOk());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
        StatementType testStatementType = statementTypeList.get(statementTypeList.size() - 1);
        assertThat(testStatementType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingStatementType() throws Exception {
        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();
        statementType.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatementType() throws Exception {
        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();
        statementType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatementType() throws Exception {
        int databaseSizeBeforeUpdate = statementTypeRepository.findAll().size();
        statementType.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(statementType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatementType in the database
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatementType() throws Exception {
        // Initialize the database
        statementTypeRepository.saveAndFlush(statementType);

        int databaseSizeBeforeDelete = statementTypeRepository.findAll().size();

        // Delete the statementType
        restStatementTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, statementType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StatementType> statementTypeList = statementTypeRepository.findAll();
        assertThat(statementTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
