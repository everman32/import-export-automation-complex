package by.victory.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.ImportProd;
import by.victory.myapp.repository.ImportProdRepository;
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
 * Integration tests for the {@link ImportProdResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ImportProdResourceIT {

    private static final Instant DEFAULT_ARRIVALDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ARRIVALDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/import-prods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ImportProdRepository importProdRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImportProdMockMvc;

    private ImportProd importProd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportProd createEntity(EntityManager em) {
        ImportProd importProd = new ImportProd().arrivaldate(DEFAULT_ARRIVALDATE);
        return importProd;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImportProd createUpdatedEntity(EntityManager em) {
        ImportProd importProd = new ImportProd().arrivaldate(UPDATED_ARRIVALDATE);
        return importProd;
    }

    @BeforeEach
    public void initTest() {
        importProd = createEntity(em);
    }

    @Test
    @Transactional
    void createImportProd() throws Exception {
        int databaseSizeBeforeCreate = importProdRepository.findAll().size();
        // Create the ImportProd
        restImportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(importProd)))
            .andExpect(status().isCreated());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeCreate + 1);
        ImportProd testImportProd = importProdList.get(importProdList.size() - 1);
        assertThat(testImportProd.getArrivaldate()).isEqualTo(DEFAULT_ARRIVALDATE);
    }

    @Test
    @Transactional
    void createImportProdWithExistingId() throws Exception {
        // Create the ImportProd with an existing ID
        importProd.setId(1L);

        int databaseSizeBeforeCreate = importProdRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(importProd)))
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkArrivaldateIsRequired() throws Exception {
        int databaseSizeBeforeTest = importProdRepository.findAll().size();
        // set the field null
        importProd.setArrivaldate(null);

        // Create the ImportProd, which fails.

        restImportProdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(importProd)))
            .andExpect(status().isBadRequest());

        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImportProds() throws Exception {
        // Initialize the database
        importProdRepository.saveAndFlush(importProd);

        // Get all the importProdList
        restImportProdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(importProd.getId().intValue())))
            .andExpect(jsonPath("$.[*].arrivaldate").value(hasItem(DEFAULT_ARRIVALDATE.toString())));
    }

    @Test
    @Transactional
    void getImportProd() throws Exception {
        // Initialize the database
        importProdRepository.saveAndFlush(importProd);

        // Get the importProd
        restImportProdMockMvc
            .perform(get(ENTITY_API_URL_ID, importProd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(importProd.getId().intValue()))
            .andExpect(jsonPath("$.arrivaldate").value(DEFAULT_ARRIVALDATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingImportProd() throws Exception {
        // Get the importProd
        restImportProdMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewImportProd() throws Exception {
        // Initialize the database
        importProdRepository.saveAndFlush(importProd);

        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();

        // Update the importProd
        ImportProd updatedImportProd = importProdRepository.findById(importProd.getId()).get();
        // Disconnect from session so that the updates on updatedImportProd are not directly saved in db
        em.detach(updatedImportProd);
        updatedImportProd.arrivaldate(UPDATED_ARRIVALDATE);

        restImportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedImportProd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedImportProd))
            )
            .andExpect(status().isOk());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
        ImportProd testImportProd = importProdList.get(importProdList.size() - 1);
        assertThat(testImportProd.getArrivaldate()).isEqualTo(UPDATED_ARRIVALDATE);
    }

    @Test
    @Transactional
    void putNonExistingImportProd() throws Exception {
        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();
        importProd.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, importProd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImportProd() throws Exception {
        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();
        importProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImportProd() throws Exception {
        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();
        importProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(importProd)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImportProdWithPatch() throws Exception {
        // Initialize the database
        importProdRepository.saveAndFlush(importProd);

        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();

        // Update the importProd using partial update
        ImportProd partialUpdatedImportProd = new ImportProd();
        partialUpdatedImportProd.setId(importProd.getId());

        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedImportProd))
            )
            .andExpect(status().isOk());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
        ImportProd testImportProd = importProdList.get(importProdList.size() - 1);
        assertThat(testImportProd.getArrivaldate()).isEqualTo(DEFAULT_ARRIVALDATE);
    }

    @Test
    @Transactional
    void fullUpdateImportProdWithPatch() throws Exception {
        // Initialize the database
        importProdRepository.saveAndFlush(importProd);

        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();

        // Update the importProd using partial update
        ImportProd partialUpdatedImportProd = new ImportProd();
        partialUpdatedImportProd.setId(importProd.getId());

        partialUpdatedImportProd.arrivaldate(UPDATED_ARRIVALDATE);

        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImportProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedImportProd))
            )
            .andExpect(status().isOk());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
        ImportProd testImportProd = importProdList.get(importProdList.size() - 1);
        assertThat(testImportProd.getArrivaldate()).isEqualTo(UPDATED_ARRIVALDATE);
    }

    @Test
    @Transactional
    void patchNonExistingImportProd() throws Exception {
        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();
        importProd.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, importProd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImportProd() throws Exception {
        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();
        importProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(importProd))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImportProd() throws Exception {
        int databaseSizeBeforeUpdate = importProdRepository.findAll().size();
        importProd.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImportProdMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(importProd))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImportProd in the database
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImportProd() throws Exception {
        // Initialize the database
        importProdRepository.saveAndFlush(importProd);

        int databaseSizeBeforeDelete = importProdRepository.findAll().size();

        // Delete the importProd
        restImportProdMockMvc
            .perform(delete(ENTITY_API_URL_ID, importProd.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ImportProd> importProdList = importProdRepository.findAll();
        assertThat(importProdList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
