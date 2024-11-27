package by.victory.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.Positioning;
import by.victory.myapp.domain.Statement;
import by.victory.myapp.repository.StatementRepository;
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
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatementMockMvc;

    private Statement statement;

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
            positioning = PositioningResourceIT.createEntity(em);
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
        Statement statement = new Statement()
            .name(UPDATED_NAME)
            .transportTariff(UPDATED_TRANSPORT_TARIFF)
            .deliveryScope(UPDATED_DELIVERY_SCOPE);
        // Add required entity
        Positioning positioning;
        if (TestUtil.findAll(em, Positioning.class).isEmpty()) {
            positioning = PositioningResourceIT.createUpdatedEntity(em);
            em.persist(positioning);
            em.flush();
        } else {
            positioning = TestUtil.findAll(em, Positioning.class).get(0);
        }
        statement.setPositioning(positioning);
        return statement;
    }

    @BeforeEach
    public void initTest() {
        statement = createEntity(em);
    }

    @Test
    @Transactional
    void createStatement() throws Exception {
        int databaseSizeBeforeCreate = statementRepository.findAll().size();
        // Create the Statement
        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statement)))
            .andExpect(status().isCreated());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeCreate + 1);
        Statement testStatement = statementList.get(statementList.size() - 1);
        assertThat(testStatement.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStatement.getTransportTariff()).isEqualTo(DEFAULT_TRANSPORT_TARIFF);
        assertThat(testStatement.getDeliveryScope()).isEqualTo(DEFAULT_DELIVERY_SCOPE);
    }

    @Test
    @Transactional
    void createStatementWithExistingId() throws Exception {
        // Create the Statement with an existing ID
        statement.setId(1L);

        int databaseSizeBeforeCreate = statementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statement)))
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = statementRepository.findAll().size();
        // set the field null
        statement.setName(null);

        // Create the Statement, which fails.

        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statement)))
            .andExpect(status().isBadRequest());

        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransportTariffIsRequired() throws Exception {
        int databaseSizeBeforeTest = statementRepository.findAll().size();
        // set the field null
        statement.setTransportTariff(null);

        // Create the Statement, which fails.

        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statement)))
            .andExpect(status().isBadRequest());

        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDeliveryScopeIsRequired() throws Exception {
        int databaseSizeBeforeTest = statementRepository.findAll().size();
        // set the field null
        statement.setDeliveryScope(null);

        // Create the Statement, which fails.

        restStatementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statement)))
            .andExpect(status().isBadRequest());

        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStatements() throws Exception {
        // Initialize the database
        statementRepository.saveAndFlush(statement);

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
        statementRepository.saveAndFlush(statement);

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
    void putNewStatement() throws Exception {
        // Initialize the database
        statementRepository.saveAndFlush(statement);

        int databaseSizeBeforeUpdate = statementRepository.findAll().size();

        // Update the statement
        Statement updatedStatement = statementRepository.findById(statement.getId()).get();
        // Disconnect from session so that the updates on updatedStatement are not directly saved in db
        em.detach(updatedStatement);
        updatedStatement.name(UPDATED_NAME).transportTariff(UPDATED_TRANSPORT_TARIFF).deliveryScope(UPDATED_DELIVERY_SCOPE);

        restStatementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStatement))
            )
            .andExpect(status().isOk());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
        Statement testStatement = statementList.get(statementList.size() - 1);
        assertThat(testStatement.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStatement.getTransportTariff()).isEqualTo(UPDATED_TRANSPORT_TARIFF);
        assertThat(testStatement.getDeliveryScope()).isEqualTo(UPDATED_DELIVERY_SCOPE);
    }

    @Test
    @Transactional
    void putNonExistingStatement() throws Exception {
        int databaseSizeBeforeUpdate = statementRepository.findAll().size();
        statement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatement() throws Exception {
        int databaseSizeBeforeUpdate = statementRepository.findAll().size();
        statement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatement() throws Exception {
        int databaseSizeBeforeUpdate = statementRepository.findAll().size();
        statement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatementWithPatch() throws Exception {
        // Initialize the database
        statementRepository.saveAndFlush(statement);

        int databaseSizeBeforeUpdate = statementRepository.findAll().size();

        // Update the statement using partial update
        Statement partialUpdatedStatement = new Statement();
        partialUpdatedStatement.setId(statement.getId());

        partialUpdatedStatement.transportTariff(UPDATED_TRANSPORT_TARIFF);

        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatement))
            )
            .andExpect(status().isOk());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
        Statement testStatement = statementList.get(statementList.size() - 1);
        assertThat(testStatement.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStatement.getTransportTariff()).isEqualTo(UPDATED_TRANSPORT_TARIFF);
        assertThat(testStatement.getDeliveryScope()).isEqualTo(DEFAULT_DELIVERY_SCOPE);
    }

    @Test
    @Transactional
    void fullUpdateStatementWithPatch() throws Exception {
        // Initialize the database
        statementRepository.saveAndFlush(statement);

        int databaseSizeBeforeUpdate = statementRepository.findAll().size();

        // Update the statement using partial update
        Statement partialUpdatedStatement = new Statement();
        partialUpdatedStatement.setId(statement.getId());

        partialUpdatedStatement.name(UPDATED_NAME).transportTariff(UPDATED_TRANSPORT_TARIFF).deliveryScope(UPDATED_DELIVERY_SCOPE);

        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatement))
            )
            .andExpect(status().isOk());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
        Statement testStatement = statementList.get(statementList.size() - 1);
        assertThat(testStatement.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStatement.getTransportTariff()).isEqualTo(UPDATED_TRANSPORT_TARIFF);
        assertThat(testStatement.getDeliveryScope()).isEqualTo(UPDATED_DELIVERY_SCOPE);
    }

    @Test
    @Transactional
    void patchNonExistingStatement() throws Exception {
        int databaseSizeBeforeUpdate = statementRepository.findAll().size();
        statement.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatement() throws Exception {
        int databaseSizeBeforeUpdate = statementRepository.findAll().size();
        statement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statement))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatement() throws Exception {
        int databaseSizeBeforeUpdate = statementRepository.findAll().size();
        statement.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatementMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(statement))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statement in the database
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatement() throws Exception {
        // Initialize the database
        statementRepository.saveAndFlush(statement);

        int databaseSizeBeforeDelete = statementRepository.findAll().size();

        // Delete the statement
        restStatementMockMvc
            .perform(delete(ENTITY_API_URL_ID, statement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Statement> statementList = statementRepository.findAll();
        assertThat(statementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
