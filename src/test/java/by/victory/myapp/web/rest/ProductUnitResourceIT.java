package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.ProductUnitAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.ProductUnit;
import by.victory.myapp.repository.ProductUnitRepository;
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
 * Integration tests for the {@link ProductUnitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductUnitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-units";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductUnitMockMvc;

    private ProductUnit productUnit;

    private ProductUnit insertedProductUnit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductUnit createEntity() {
        return new ProductUnit().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductUnit createUpdatedEntity() {
        return new ProductUnit().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        productUnit = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductUnit != null) {
            productUnitRepository.delete(insertedProductUnit);
            insertedProductUnit = null;
        }
    }

    @Test
    @Transactional
    void createProductUnit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductUnit
        var returnedProductUnit = om.readValue(
            restProductUnitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productUnit)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductUnit.class
        );

        // Validate the ProductUnit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductUnitUpdatableFieldsEquals(returnedProductUnit, getPersistedProductUnit(returnedProductUnit));

        insertedProductUnit = returnedProductUnit;
    }

    @Test
    @Transactional
    void createProductUnitWithExistingId() throws Exception {
        // Create the ProductUnit with an existing ID
        productUnit.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productUnit)))
            .andExpect(status().isBadRequest());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productUnit.setName(null);

        // Create the ProductUnit, which fails.

        restProductUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productUnit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productUnit.setDescription(null);

        // Create the ProductUnit, which fails.

        restProductUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productUnit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductUnits() throws Exception {
        // Initialize the database
        insertedProductUnit = productUnitRepository.saveAndFlush(productUnit);

        // Get all the productUnitList
        restProductUnitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getProductUnit() throws Exception {
        // Initialize the database
        insertedProductUnit = productUnitRepository.saveAndFlush(productUnit);

        // Get the productUnit
        restProductUnitMockMvc
            .perform(get(ENTITY_API_URL_ID, productUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingProductUnit() throws Exception {
        // Get the productUnit
        restProductUnitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductUnit() throws Exception {
        // Initialize the database
        insertedProductUnit = productUnitRepository.saveAndFlush(productUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productUnit
        ProductUnit updatedProductUnit = productUnitRepository.findById(productUnit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductUnit are not directly saved in db
        em.detach(updatedProductUnit);
        updatedProductUnit.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restProductUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductUnit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProductUnit))
            )
            .andExpect(status().isOk());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductUnitToMatchAllProperties(updatedProductUnit);
    }

    @Test
    @Transactional
    void putNonExistingProductUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productUnit.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productUnit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productUnit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductUnitWithPatch() throws Exception {
        // Initialize the database
        insertedProductUnit = productUnitRepository.saveAndFlush(productUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productUnit using partial update
        ProductUnit partialUpdatedProductUnit = new ProductUnit();
        partialUpdatedProductUnit.setId(productUnit.getId());

        partialUpdatedProductUnit.description(UPDATED_DESCRIPTION);

        restProductUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductUnit))
            )
            .andExpect(status().isOk());

        // Validate the ProductUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUnitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductUnit, productUnit),
            getPersistedProductUnit(productUnit)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductUnitWithPatch() throws Exception {
        // Initialize the database
        insertedProductUnit = productUnitRepository.saveAndFlush(productUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productUnit using partial update
        ProductUnit partialUpdatedProductUnit = new ProductUnit();
        partialUpdatedProductUnit.setId(productUnit.getId());

        partialUpdatedProductUnit.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restProductUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductUnit))
            )
            .andExpect(status().isOk());

        // Validate the ProductUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductUnitUpdatableFieldsEquals(partialUpdatedProductUnit, getPersistedProductUnit(partialUpdatedProductUnit));
    }

    @Test
    @Transactional
    void patchNonExistingProductUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productUnit.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productUnit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductUnit() throws Exception {
        // Initialize the database
        insertedProductUnit = productUnitRepository.saveAndFlush(productUnit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productUnit
        restProductUnitMockMvc
            .perform(delete(ENTITY_API_URL_ID, productUnit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productUnitRepository.count();
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

    protected ProductUnit getPersistedProductUnit(ProductUnit productUnit) {
        return productUnitRepository.findById(productUnit.getId()).orElseThrow();
    }

    protected void assertPersistedProductUnitToMatchAllProperties(ProductUnit expectedProductUnit) {
        assertProductUnitAllPropertiesEquals(expectedProductUnit, getPersistedProductUnit(expectedProductUnit));
    }

    protected void assertPersistedProductUnitToMatchUpdatableProperties(ProductUnit expectedProductUnit) {
        assertProductUnitAllUpdatablePropertiesEquals(expectedProductUnit, getPersistedProductUnit(expectedProductUnit));
    }
}
