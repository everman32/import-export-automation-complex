package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.TransportAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.Transport;
import by.victory.myapp.repository.TransportRepository;
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
 * Integration tests for the {@link TransportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransportResourceIT {

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_VIN = "AAAAAAAAAAAAAAAAA";
    private static final String UPDATED_VIN = "BBBBBBBBBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransportMockMvc;

    private Transport transport;

    private Transport insertedTransport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transport createEntity() {
        return new Transport().brand(DEFAULT_BRAND).model(DEFAULT_MODEL).vin(DEFAULT_VIN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transport createUpdatedEntity() {
        return new Transport().brand(UPDATED_BRAND).model(UPDATED_MODEL).vin(UPDATED_VIN);
    }

    @BeforeEach
    public void initTest() {
        transport = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTransport != null) {
            transportRepository.delete(insertedTransport);
            insertedTransport = null;
        }
    }

    @Test
    @Transactional
    void createTransport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Transport
        var returnedTransport = om.readValue(
            restTransportMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Transport.class
        );

        // Validate the Transport in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTransportUpdatableFieldsEquals(returnedTransport, getPersistedTransport(returnedTransport));

        insertedTransport = returnedTransport;
    }

    @Test
    @Transactional
    void createTransportWithExistingId() throws Exception {
        // Create the Transport with an existing ID
        transport.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport)))
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBrandIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transport.setBrand(null);

        // Create the Transport, which fails.

        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transport.setModel(null);

        // Create the Transport, which fails.

        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVinIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transport.setVin(null);

        // Create the Transport, which fails.

        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransports() throws Exception {
        // Initialize the database
        insertedTransport = transportRepository.saveAndFlush(transport);

        // Get all the transportList
        restTransportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transport.getId().intValue())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].vin").value(hasItem(DEFAULT_VIN)));
    }

    @Test
    @Transactional
    void getTransport() throws Exception {
        // Initialize the database
        insertedTransport = transportRepository.saveAndFlush(transport);

        // Get the transport
        restTransportMockMvc
            .perform(get(ENTITY_API_URL_ID, transport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transport.getId().intValue()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.vin").value(DEFAULT_VIN));
    }

    @Test
    @Transactional
    void getNonExistingTransport() throws Exception {
        // Get the transport
        restTransportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransport() throws Exception {
        // Initialize the database
        insertedTransport = transportRepository.saveAndFlush(transport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transport
        Transport updatedTransport = transportRepository.findById(transport.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransport are not directly saved in db
        em.detach(updatedTransport);
        updatedTransport.brand(UPDATED_BRAND).model(UPDATED_MODEL).vin(UPDATED_VIN);

        restTransportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTransport))
            )
            .andExpect(status().isOk());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransportToMatchAllProperties(updatedTransport);
    }

    @Test
    @Transactional
    void putNonExistingTransport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transport.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transport.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransportWithPatch() throws Exception {
        // Initialize the database
        insertedTransport = transportRepository.saveAndFlush(transport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transport using partial update
        Transport partialUpdatedTransport = new Transport();
        partialUpdatedTransport.setId(transport.getId());

        partialUpdatedTransport.vin(UPDATED_VIN);

        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransport))
            )
            .andExpect(status().isOk());

        // Validate the Transport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransportUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransport, transport),
            getPersistedTransport(transport)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransportWithPatch() throws Exception {
        // Initialize the database
        insertedTransport = transportRepository.saveAndFlush(transport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transport using partial update
        Transport partialUpdatedTransport = new Transport();
        partialUpdatedTransport.setId(transport.getId());

        partialUpdatedTransport.brand(UPDATED_BRAND).model(UPDATED_MODEL).vin(UPDATED_VIN);

        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransport))
            )
            .andExpect(status().isOk());

        // Validate the Transport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransportUpdatableFieldsEquals(partialUpdatedTransport, getPersistedTransport(partialUpdatedTransport));
    }

    @Test
    @Transactional
    void patchNonExistingTransport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transport.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransport() throws Exception {
        // Initialize the database
        insertedTransport = transportRepository.saveAndFlush(transport);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transport
        restTransportMockMvc
            .perform(delete(ENTITY_API_URL_ID, transport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transportRepository.count();
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

    protected Transport getPersistedTransport(Transport transport) {
        return transportRepository.findById(transport.getId()).orElseThrow();
    }

    protected void assertPersistedTransportToMatchAllProperties(Transport expectedTransport) {
        assertTransportAllPropertiesEquals(expectedTransport, getPersistedTransport(expectedTransport));
    }

    protected void assertPersistedTransportToMatchUpdatableProperties(Transport expectedTransport) {
        assertTransportAllUpdatablePropertiesEquals(expectedTransport, getPersistedTransport(expectedTransport));
    }
}
