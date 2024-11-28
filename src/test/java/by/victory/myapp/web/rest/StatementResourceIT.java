package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.StatementAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.Positioning;
import by.victory.myapp.domain.Statement;
import by.victory.myapp.repository.StatementRepository;
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
 * Integration tests for the {@link StatementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatementResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_TRANSPORT_TARIFF = 1D;
    private static final Double UPDATED_TRANSPORT_TARIFF = 2D;

    private static final Double DEFAULT_DELIVERY_SCOPE = 1D;
    private static final Double UPDATED_DELIVERY_SCOPE = 2D;

    private static final String ENTITY_API_URL = "/api/statements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatementMockMvc;

    private Statement statement;

    private Statement insertedStatement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Statement createEntity(EntityManager em) {
        Statement statement = new Statement()
            .name(DEFAULT_NAME)
            .transportTariff(DEFAULT_TRANSPORT_TARIFF)
            .deliveryScope(DEFAULT_DELIVERY_SCOPE);
        // Add required entity
        Positioning positioning;
        if (TestUtil.findAll(em, Positioning.class).isEmpty()) {
            positioning = PositioningResourceIT.createEntity();
            em.persist(positioning);
            em.flush();
        } else {
            positioning = TestUtil.findAll(em, Positioning.class).get(0);
        }
        statement.setPositioning(positioning);
        return statement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Statement createUpdatedEntity(EntityManager em) {
        Statement updatedStatement = new Statement()
            .name(UPDATED_NAME)
            .transportTariff(UPDATED_TRANSPORT_TARIFF)
            .deliveryScope(UPDATED_DELIVERY_SCOPE);
        // Add required entity
        Positioning positioning;
        if (TestUtil.findAll(em, Positioning.class).isEmpty()) {
            positioning = PositioningResourceIT.createUpdatedEntity();
            em.persist(positioning);
            em.flush();
        } else {
            positioning = TestUtil.findAll(em, Positioning.class).get(0);
        }
        updatedStatement.setPositioning(positioning);
        return updatedStatement;
    }

    @BeforeEach
    public void initTest() {
        statement = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedStatement != null) {
            statementRepository.delete(insertedStatement);
            insertedStatement = null;
        }
    }

    @Test
    @Transactional
    void createStatement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Statement
        var returnedStatement = om.readValue(
            restStatementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Statement.class
        );

        // Validate the Statement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStatementUpdatableFieldsEquals(returnedStatement, getPersistedStatement(returnedStatement));

        insertedStatement = returnedStatement;
    }

    @Test
    @Transactional
    void createStatementWithExistingId() throws Exception {
        // Create the Statement with an existing ID
        statement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement)))
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statement.setName(null);

        // Create the Statement, which fails.

        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransportTariffIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statement.setTransportTariff(null);

        // Create the Statement, which fails.

        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDeliveryScopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        statement.setDeliveryScope(null);

        // Create the Statement, which fails.

        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStatements() throws Exception {
        // Initialize the database
        insertedStatement = statementRepository.saveAndFlush(statement);

        // Get all the statementList
        restStatementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].transportTariff").value(hasItem(DEFAULT_TRANSPORT_TARIFF.doubleValue())))
            .andExpect(jsonPath("$.[*].deliveryScope").value(hasItem(DEFAULT_DELIVERY_SCOPE.doubleValue())));
    }

    @Test
    @Transactional
    void getStatement() throws Exception {
        // Initialize the database
        insertedStatement = statementRepository.saveAndFlush(statement);

        // Get the statement
        restStatementMockMvc
            .perform(get(ENTITY_API_URL_ID, statement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statement.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.transportTariff").value(DEFAULT_TRANSPORT_TARIFF.doubleValue()))
            .andExpect(jsonPath("$.deliveryScope").value(DEFAULT_DELIVERY_SCOPE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingStatement() throws Exception {
        // Get the statement
        restStatementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatement() throws Exception {
        // Initialize the database
        insertedStatement = statementRepository.saveAndFlush(statement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statement
        Statement updatedStatement = statementRepository.findById(statement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatement are not directly saved in db
        em.detach(updatedStatement);
        updatedStatement.name(UPDATED_NAME).transportTariff(UPDATED_TRANSPORT_TARIFF).deliveryScope(UPDATED_DELIVERY_SCOPE);

        restStatementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStatement))
            )
            .andExpect(status().isOk());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStatementToMatchAllProperties(updatedStatement);
    }

    @Test
    @Transactional
    void putNonExistingStatement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statement.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(statement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatementWithPatch() throws Exception {
        // Initialize the database
        insertedStatement = statementRepository.saveAndFlush(statement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statement using partial update
        Statement partialUpdatedStatement = new Statement();
        partialUpdatedStatement.setId(statement.getId());

        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatement))
            )
            .andExpect(status().isOk());

        // Validate the Statement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStatement, statement),
            getPersistedStatement(statement)
        );
    }

    @Test
    @Transactional
    void fullUpdateStatementWithPatch() throws Exception {
        // Initialize the database
        insertedStatement = statementRepository.saveAndFlush(statement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the statement using partial update
        Statement partialUpdatedStatement = new Statement();
        partialUpdatedStatement.setId(statement.getId());

        partialUpdatedStatement.name(UPDATED_NAME).transportTariff(UPDATED_TRANSPORT_TARIFF).deliveryScope(UPDATED_DELIVERY_SCOPE);

        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStatement))
            )
            .andExpect(status().isOk());

        // Validate the Statement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStatementUpdatableFieldsEquals(partialUpdatedStatement, getPersistedStatement(partialUpdatedStatement));
    }

    @Test
    @Transactional
    void patchNonExistingStatement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        statement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(statement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatement() throws Exception {
        // Initialize the database
        insertedStatement = statementRepository.saveAndFlush(statement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the statement
        restStatementMockMvc
            .perform(delete(ENTITY_API_URL_ID, statement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return statementRepository.count();
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

    protected Statement getPersistedStatement(Statement statement) {
        return statementRepository.findById(statement.getId()).orElseThrow();
    }

    protected void assertPersistedStatementToMatchAllProperties(Statement expectedStatement) {
        assertStatementAllPropertiesEquals(expectedStatement, getPersistedStatement(expectedStatement));
    }

    protected void assertPersistedStatementToMatchUpdatableProperties(Statement expectedStatement) {
        assertStatementAllUpdatablePropertiesEquals(expectedStatement, getPersistedStatement(expectedStatement));
    }
}
