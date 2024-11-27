package by.victory.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.Transport;
import by.victory.myapp.repository.TransportRepository;
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
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransportMockMvc;

    private Transport transport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transport createEntity(EntityManager em) {
        Transport transport = new Transport().brand(DEFAULT_BRAND).model(DEFAULT_MODEL).vin(DEFAULT_VIN);
        return transport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transport createUpdatedEntity(EntityManager em) {
        Transport transport = new Transport().brand(UPDATED_BRAND).model(UPDATED_MODEL).vin(UPDATED_VIN);
        return transport;
    }

    @BeforeEach
    public void initTest() {
        transport = createEntity(em);
    }

    @Test
    @Transactional
    void createTransport() throws Exception {
        int databaseSizeBeforeCreate = transportRepository.findAll().size();
        // Create the Transport
        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transport)))
            .andExpect(status().isCreated());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeCreate + 1);
        Transport testTransport = transportList.get(transportList.size() - 1);
        assertThat(testTransport.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testTransport.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testTransport.getVin()).isEqualTo(DEFAULT_VIN);
    }

    @Test
    @Transactional
    void createTransportWithExistingId() throws Exception {
        // Create the Transport with an existing ID
        transport.setId(1L);

        int databaseSizeBeforeCreate = transportRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transport)))
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBrandIsRequired() throws Exception {
        int databaseSizeBeforeTest = transportRepository.findAll().size();
        // set the field null
        transport.setBrand(null);

        // Create the Transport, which fails.

        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transport)))
            .andExpect(status().isBadRequest());

        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModelIsRequired() throws Exception {
        int databaseSizeBeforeTest = transportRepository.findAll().size();
        // set the field null
        transport.setModel(null);

        // Create the Transport, which fails.

        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transport)))
            .andExpect(status().isBadRequest());

        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVinIsRequired() throws Exception {
        int databaseSizeBeforeTest = transportRepository.findAll().size();
        // set the field null
        transport.setVin(null);

        // Create the Transport, which fails.

        restTransportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transport)))
            .andExpect(status().isBadRequest());

        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransports() throws Exception {
        // Initialize the database
        transportRepository.saveAndFlush(transport);

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
        transportRepository.saveAndFlush(transport);

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
    void putNewTransport() throws Exception {
        // Initialize the database
        transportRepository.saveAndFlush(transport);

        int databaseSizeBeforeUpdate = transportRepository.findAll().size();

        // Update the transport
        Transport updatedTransport = transportRepository.findById(transport.getId()).get();
        // Disconnect from session so that the updates on updatedTransport are not directly saved in db
        em.detach(updatedTransport);
        updatedTransport.brand(UPDATED_BRAND).model(UPDATED_MODEL).vin(UPDATED_VIN);

        restTransportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransport))
            )
            .andExpect(status().isOk());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
        Transport testTransport = transportList.get(transportList.size() - 1);
        assertThat(testTransport.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testTransport.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testTransport.getVin()).isEqualTo(UPDATED_VIN);
    }

    @Test
    @Transactional
    void putNonExistingTransport() throws Exception {
        int databaseSizeBeforeUpdate = transportRepository.findAll().size();
        transport.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransport() throws Exception {
        int databaseSizeBeforeUpdate = transportRepository.findAll().size();
        transport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransport() throws Exception {
        int databaseSizeBeforeUpdate = transportRepository.findAll().size();
        transport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransportWithPatch() throws Exception {
        // Initialize the database
        transportRepository.saveAndFlush(transport);

        int databaseSizeBeforeUpdate = transportRepository.findAll().size();

        // Update the transport using partial update
        Transport partialUpdatedTransport = new Transport();
        partialUpdatedTransport.setId(transport.getId());

        partialUpdatedTransport.vin(UPDATED_VIN);

        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransport.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransport))
            )
            .andExpect(status().isOk());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
        Transport testTransport = transportList.get(transportList.size() - 1);
        assertThat(testTransport.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testTransport.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testTransport.getVin()).isEqualTo(UPDATED_VIN);
    }

    @Test
    @Transactional
    void fullUpdateTransportWithPatch() throws Exception {
        // Initialize the database
        transportRepository.saveAndFlush(transport);

        int databaseSizeBeforeUpdate = transportRepository.findAll().size();

        // Update the transport using partial update
        Transport partialUpdatedTransport = new Transport();
        partialUpdatedTransport.setId(transport.getId());

        partialUpdatedTransport.brand(UPDATED_BRAND).model(UPDATED_MODEL).vin(UPDATED_VIN);

        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransport.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransport))
            )
            .andExpect(status().isOk());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
        Transport testTransport = transportList.get(transportList.size() - 1);
        assertThat(testTransport.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testTransport.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testTransport.getVin()).isEqualTo(UPDATED_VIN);
    }

    @Test
    @Transactional
    void patchNonExistingTransport() throws Exception {
        int databaseSizeBeforeUpdate = transportRepository.findAll().size();
        transport.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transport.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransport() throws Exception {
        int databaseSizeBeforeUpdate = transportRepository.findAll().size();
        transport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransport() throws Exception {
        int databaseSizeBeforeUpdate = transportRepository.findAll().size();
        transport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransportMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transport))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transport in the database
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransport() throws Exception {
        // Initialize the database
        transportRepository.saveAndFlush(transport);

        int databaseSizeBeforeDelete = transportRepository.findAll().size();

        // Delete the transport
        restTransportMockMvc
            .perform(delete(ENTITY_API_URL_ID, transport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transport> transportList = transportRepository.findAll();
        assertThat(transportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
