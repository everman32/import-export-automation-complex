package by.victory.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.ExportProd;
import by.victory.myapp.repository.ExportProdRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ExportProdResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExportProdResourceIT {

    private static final Instant DEFAULT_DEPARTUREDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPARTUREDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/export-prods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExportProdRepository exportProdRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExportProdMockMvc;

    private ExportProd exportProd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExportProd createEntity(EntityManager em) {
        ExportProd exportProd = new ExportProd().departuredate(DEFAULT_DEPARTUREDATE);
        return exportProd;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExportProd createUpdatedEntity(EntityManager em) {
        ExportProd exportProd = new ExportProd().departuredate(UPDATED_DEPARTUREDATE);
        return exportProd;
    }

    @BeforeEach
    public void initTest() {
        exportProd = createEntity(em);
    }

    @Test
    @Transactional
    void createExportProd() throws Exception {
        int databaseSizeBeforeCreate = exportProdRepository.findAll().size();
        // Create the ExportProd
        restExportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exportProd)))
            .andExpect(status().isCreated());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeCreate + 1);
        ExportProd testExportProd = exportProdList.get(exportProdList.size() - 1);
        assertThat(testExportProd.getDeparturedate()).isEqualTo(DEFAULT_DEPARTUREDATE);
    }

    @Test
    @Transactional
    void createExportProdWithExistingId() throws Exception {
        // Create the ExportProd with an existing ID
        exportProd.setId(1L);

        int databaseSizeBeforeCreate = exportProdRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exportProd)))
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDeparturedateIsRequired() throws Exception {
        int databaseSizeBeforeTest = exportProdRepository.findAll().size();
        // set the field null
        exportProd.setDeparturedate(null);

        // Create the ExportProd, which fails.

        restExportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exportProd)))
            .andExpect(status().isBadRequest());

        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExportProds() throws Exception {
        // Initialize the database
        exportProdRepository.saveAndFlush(exportProd);

        // Get all the exportProdList
        restExportProdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exportProd.getId().intValue())))
            .andExpect(jsonPath("$.[*].departuredate").value(hasItem(DEFAULT_DEPARTUREDATE.toString())));
    }

    @Test
    @Transactional
    void getExportProd() throws Exception {
        // Initialize the database
        exportProdRepository.saveAndFlush(exportProd);

        // Get the exportProd
        restExportProdMockMvc
            .perform(get(ENTITY_API_URL_ID, exportProd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exportProd.getId().intValue()))
            .andExpect(jsonPath("$.departuredate").value(DEFAULT_DEPARTUREDATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExportProd() throws Exception {
        // Get the exportProd
        restExportProdMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExportProd() throws Exception {
        // Initialize the database
        exportProdRepository.saveAndFlush(exportProd);

        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();

        // Update the exportProd
        ExportProd updatedExportProd = exportProdRepository.findById(exportProd.getId()).get();
        // Disconnect from session so that the updates on updatedExportProd are not directly saved in db
        em.detach(updatedExportProd);
        updatedExportProd.departuredate(UPDATED_DEPARTUREDATE);

        restExportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExportProd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExportProd))
            )
            .andExpect(status().isOk());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
        ExportProd testExportProd = exportProdList.get(exportProdList.size() - 1);
        assertThat(testExportProd.getDeparturedate()).isEqualTo(UPDATED_DEPARTUREDATE);
    }

    @Test
    @Transactional
    void putNonExistingExportProd() throws Exception {
        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();
        exportProd.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exportProd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExportProd() throws Exception {
        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();
        exportProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExportProd() throws Exception {
        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();
        exportProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exportProd)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExportProdWithPatch() throws Exception {
        // Initialize the database
        exportProdRepository.saveAndFlush(exportProd);

        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();

        // Update the exportProd using partial update
        ExportProd partialUpdatedExportProd = new ExportProd();
        partialUpdatedExportProd.setId(exportProd.getId());

        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExportProd))
            )
            .andExpect(status().isOk());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
        ExportProd testExportProd = exportProdList.get(exportProdList.size() - 1);
        assertThat(testExportProd.getDeparturedate()).isEqualTo(DEFAULT_DEPARTUREDATE);
    }

    @Test
    @Transactional
    void fullUpdateExportProdWithPatch() throws Exception {
        // Initialize the database
        exportProdRepository.saveAndFlush(exportProd);

        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();

        // Update the exportProd using partial update
        ExportProd partialUpdatedExportProd = new ExportProd();
        partialUpdatedExportProd.setId(exportProd.getId());

        partialUpdatedExportProd.departuredate(UPDATED_DEPARTUREDATE);

        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExportProd))
            )
            .andExpect(status().isOk());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
        ExportProd testExportProd = exportProdList.get(exportProdList.size() - 1);
        assertThat(testExportProd.getDeparturedate()).isEqualTo(UPDATED_DEPARTUREDATE);
    }

    @Test
    @Transactional
    void patchNonExistingExportProd() throws Exception {
        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();
        exportProd.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExportProd() throws Exception {
        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();
        exportProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exportProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExportProd() throws Exception {
        int databaseSizeBeforeUpdate = exportProdRepository.findAll().size();
        exportProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExportProdMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(exportProd))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExportProd in the database
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExportProd() throws Exception {
        // Initialize the database
        exportProdRepository.saveAndFlush(exportProd);

        int databaseSizeBeforeDelete = exportProdRepository.findAll().size();

        // Delete the exportProd
        restExportProdMockMvc
            .perform(delete(ENTITY_API_URL_ID, exportProd.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ExportProd> exportProdList = exportProdRepository.findAll();
        assertThat(exportProdList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
