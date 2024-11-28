package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.ImportProdAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.ImportProd;
import by.victory.myapp.repository.ImportProdRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ImportProdResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ImportProdResourceIT {

    private static final Instant DEFAULT_ARRIVAL_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ARRIVAL_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/import-prods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImportProdRepository importProdRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportProdMockMvc;

    private ImportProd importProd;

    private ImportProd insertedImportProd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportProd createEntity() {
        return new ImportProd().arrivalDate(DEFAULT_ARRIVAL_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportProd createUpdatedEntity() {
        return new ImportProd().arrivalDate(UPDATED_ARRIVAL_DATE);
    }

    @BeforeEach
    public void initTest() {
        importProd = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedImportProd != null) {
            importProdRepository.delete(insertedImportProd);
            insertedImportProd = null;
        }
    }

    @Test
    @Transactional
    void createImportProd() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImportProd
        var returnedImportProd = om.readValue(
            restImportProdMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importProd)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImportProd.class
        );

        // Validate the ImportProd in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertImportProdUpdatableFieldsEquals(returnedImportProd, getPersistedImportProd(returnedImportProd));

        insertedImportProd = returnedImportProd;
    }

    @Test
    @Transactional
    void createImportProdWithExistingId() throws Exception {
        // Create the ImportProd with an existing ID
        importProd.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importProd)))
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkArrivalDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        importProd.setArrivalDate(null);

        // Create the ImportProd, which fails.

        restImportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importProd)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImportProds() throws Exception {
        // Initialize the database
        insertedImportProd = importProdRepository.saveAndFlush(importProd);

        // Get all the importProdList
        restImportProdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importProd.getId().intValue())))
            .andExpect(jsonPath("$.[*].arrivalDate").value(hasItem(DEFAULT_ARRIVAL_DATE.toString())));
    }

    @Test
    @Transactional
    void getImportProd() throws Exception {
        // Initialize the database
        insertedImportProd = importProdRepository.saveAndFlush(importProd);

        // Get the importProd
        restImportProdMockMvc
            .perform(get(ENTITY_API_URL_ID, importProd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(importProd.getId().intValue()))
            .andExpect(jsonPath("$.arrivalDate").value(DEFAULT_ARRIVAL_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingImportProd() throws Exception {
        // Get the importProd
        restImportProdMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImportProd() throws Exception {
        // Initialize the database
        insertedImportProd = importProdRepository.saveAndFlush(importProd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importProd
        ImportProd updatedImportProd = importProdRepository.findById(importProd.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedImportProd are not directly saved in db
        em.detach(updatedImportProd);
        updatedImportProd.arrivalDate(UPDATED_ARRIVAL_DATE);

        restImportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedImportProd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedImportProd))
            )
            .andExpect(status().isOk());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImportProdToMatchAllProperties(updatedImportProd);
    }

    @Test
    @Transactional
    void putNonExistingImportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importProd.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importProd.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(importProd)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportProdWithPatch() throws Exception {
        // Initialize the database
        insertedImportProd = importProdRepository.saveAndFlush(importProd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importProd using partial update
        ImportProd partialUpdatedImportProd = new ImportProd();
        partialUpdatedImportProd.setId(importProd.getId());

        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportProd))
            )
            .andExpect(status().isOk());

        // Validate the ImportProd in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportProdUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedImportProd, importProd),
            getPersistedImportProd(importProd)
        );
    }

    @Test
    @Transactional
    void fullUpdateImportProdWithPatch() throws Exception {
        // Initialize the database
        insertedImportProd = importProdRepository.saveAndFlush(importProd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the importProd using partial update
        ImportProd partialUpdatedImportProd = new ImportProd();
        partialUpdatedImportProd.setId(importProd.getId());

        partialUpdatedImportProd.arrivalDate(UPDATED_ARRIVAL_DATE);

        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImportProd))
            )
            .andExpect(status().isOk());

        // Validate the ImportProd in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImportProdUpdatableFieldsEquals(partialUpdatedImportProd, getPersistedImportProd(partialUpdatedImportProd));
    }

    @Test
    @Transactional
    void patchNonExistingImportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importProd.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        importProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(importProd)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportProd() throws Exception {
        // Initialize the database
        insertedImportProd = importProdRepository.saveAndFlush(importProd);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the importProd
        restImportProdMockMvc
            .perform(delete(ENTITY_API_URL_ID, importProd.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return importProdRepository.count();
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

    protected ImportProd getPersistedImportProd(ImportProd importProd) {
        return importProdRepository.findById(importProd.getId()).orElseThrow();
    }

    protected void assertPersistedImportProdToMatchAllProperties(ImportProd expectedImportProd) {
        assertImportProdAllPropertiesEquals(expectedImportProd, getPersistedImportProd(expectedImportProd));
    }

    protected void assertPersistedImportProdToMatchUpdatableProperties(ImportProd expectedImportProd) {
        assertImportProdAllUpdatablePropertiesEquals(expectedImportProd, getPersistedImportProd(expectedImportProd));
    }
}
