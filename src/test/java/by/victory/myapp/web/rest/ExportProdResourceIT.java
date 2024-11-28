package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.ExportProdAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.ExportProd;
import by.victory.myapp.repository.ExportProdRepository;
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
 * Integration tests for the {@link ExportProdResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExportProdResourceIT {

    private static final Instant DEFAULT_DEPARTURE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPARTURE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/export-prods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExportProdRepository exportProdRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExportProdMockMvc;

    private ExportProd exportProd;

    private ExportProd insertedExportProd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExportProd createEntity() {
        return new ExportProd().departureDate(DEFAULT_DEPARTURE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExportProd createUpdatedEntity() {
        return new ExportProd().departureDate(UPDATED_DEPARTURE_DATE);
    }

    @BeforeEach
    public void initTest() {
        exportProd = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedExportProd != null) {
            exportProdRepository.delete(insertedExportProd);
            insertedExportProd = null;
        }
    }

    @Test
    @Transactional
    void createExportProd() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExportProd
        var returnedExportProd = om.readValue(
            restExportProdMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exportProd)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExportProd.class
        );

        // Validate the ExportProd in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertExportProdUpdatableFieldsEquals(returnedExportProd, getPersistedExportProd(returnedExportProd));

        insertedExportProd = returnedExportProd;
    }

    @Test
    @Transactional
    void createExportProdWithExistingId() throws Exception {
        // Create the ExportProd with an existing ID
        exportProd.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exportProd)))
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDepartureDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        exportProd.setDepartureDate(null);

        // Create the ExportProd, which fails.

        restExportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exportProd)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExportProds() throws Exception {
        // Initialize the database
        insertedExportProd = exportProdRepository.saveAndFlush(exportProd);

        // Get all the exportProdList
        restExportProdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exportProd.getId().intValue())))
            .andExpect(jsonPath("$.[*].departureDate").value(hasItem(DEFAULT_DEPARTURE_DATE.toString())));
    }

    @Test
    @Transactional
    void getExportProd() throws Exception {
        // Initialize the database
        insertedExportProd = exportProdRepository.saveAndFlush(exportProd);

        // Get the exportProd
        restExportProdMockMvc
            .perform(get(ENTITY_API_URL_ID, exportProd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exportProd.getId().intValue()))
            .andExpect(jsonPath("$.departureDate").value(DEFAULT_DEPARTURE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExportProd() throws Exception {
        // Get the exportProd
        restExportProdMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExportProd() throws Exception {
        // Initialize the database
        insertedExportProd = exportProdRepository.saveAndFlush(exportProd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exportProd
        ExportProd updatedExportProd = exportProdRepository.findById(exportProd.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExportProd are not directly saved in db
        em.detach(updatedExportProd);
        updatedExportProd.departureDate(UPDATED_DEPARTURE_DATE);

        restExportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExportProd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedExportProd))
            )
            .andExpect(status().isOk());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExportProdToMatchAllProperties(updatedExportProd);
    }

    @Test
    @Transactional
    void putNonExistingExportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exportProd.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exportProd.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exportProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exportProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(exportProd)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExportProdWithPatch() throws Exception {
        // Initialize the database
        insertedExportProd = exportProdRepository.saveAndFlush(exportProd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exportProd using partial update
        ExportProd partialUpdatedExportProd = new ExportProd();
        partialUpdatedExportProd.setId(exportProd.getId());

        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExportProd))
            )
            .andExpect(status().isOk());

        // Validate the ExportProd in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExportProdUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExportProd, exportProd),
            getPersistedExportProd(exportProd)
        );
    }

    @Test
    @Transactional
    void fullUpdateExportProdWithPatch() throws Exception {
        // Initialize the database
        insertedExportProd = exportProdRepository.saveAndFlush(exportProd);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the exportProd using partial update
        ExportProd partialUpdatedExportProd = new ExportProd();
        partialUpdatedExportProd.setId(exportProd.getId());

        partialUpdatedExportProd.departureDate(UPDATED_DEPARTURE_DATE);

        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExportProd))
            )
            .andExpect(status().isOk());

        // Validate the ExportProd in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExportProdUpdatableFieldsEquals(partialUpdatedExportProd, getPersistedExportProd(partialUpdatedExportProd));
    }

    @Test
    @Transactional
    void patchNonExistingExportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exportProd.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exportProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExportProd() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        exportProd.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(exportProd)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExportProd in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExportProd() throws Exception {
        // Initialize the database
        insertedExportProd = exportProdRepository.saveAndFlush(exportProd);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the exportProd
        restExportProdMockMvc
            .perform(delete(ENTITY_API_URL_ID, exportProd.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return exportProdRepository.count();
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

    protected ExportProd getPersistedExportProd(ExportProd exportProd) {
        return exportProdRepository.findById(exportProd.getId()).orElseThrow();
    }

    protected void assertPersistedExportProdToMatchAllProperties(ExportProd expectedExportProd) {
        assertExportProdAllPropertiesEquals(expectedExportProd, getPersistedExportProd(expectedExportProd));
    }

    protected void assertPersistedExportProdToMatchUpdatableProperties(ExportProd expectedExportProd) {
        assertExportProdAllUpdatablePropertiesEquals(expectedExportProd, getPersistedExportProd(expectedExportProd));
    }
}
