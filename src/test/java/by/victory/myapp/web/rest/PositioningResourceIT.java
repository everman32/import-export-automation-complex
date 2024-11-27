package by.victory.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.Positioning;
import by.victory.myapp.repository.PositioningRepository;
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
 * Integration tests for the {@link PositioningResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PositioningResourceIT {

    private static final Double DEFAULT_LATITUDE = -90D;
    private static final Double UPDATED_LATITUDE = -89D;

    private static final Double DEFAULT_LONGITUDE = -180D;
    private static final Double UPDATED_LONGITUDE = -179D;

    private static final String ENTITY_API_URL = "/api/positionings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PositioningRepository positioningRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPositioningMockMvc;

    private Positioning positioning;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Positioning createEntity(EntityManager em) {
        Positioning positioning = new Positioning().latitude(DEFAULT_LATITUDE).longitude(DEFAULT_LONGITUDE);
        return positioning;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Positioning createUpdatedEntity(EntityManager em) {
        Positioning positioning = new Positioning().latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);
        return positioning;
    }

    @BeforeEach
    public void initTest() {
        positioning = createEntity(em);
    }

    @Test
    @Transactional
    void createPositioning() throws Exception {
        int databaseSizeBeforeCreate = positioningRepository.findAll().size();
        // Create the Positioning
        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positioning)))
            .andExpect(status().isCreated());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeCreate + 1);
        Positioning testPositioning = positioningList.get(positioningList.size() - 1);
        assertThat(testPositioning.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testPositioning.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void createPositioningWithExistingId() throws Exception {
        // Create the Positioning with an existing ID
        positioning.setId(1L);

        int databaseSizeBeforeCreate = positioningRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positioning)))
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = positioningRepository.findAll().size();
        // set the field null
        positioning.setLatitude(null);

        // Create the Positioning, which fails.

        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positioning)))
            .andExpect(status().isBadRequest());

        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = positioningRepository.findAll().size();
        // set the field null
        positioning.setLongitude(null);

        // Create the Positioning, which fails.

        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positioning)))
            .andExpect(status().isBadRequest());

        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPositionings() throws Exception {
        // Initialize the database
        positioningRepository.saveAndFlush(positioning);

        // Get all the positioningList
        restPositioningMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(positioning.getId().intValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }

    @Test
    @Transactional
    void getPositioning() throws Exception {
        // Initialize the database
        positioningRepository.saveAndFlush(positioning);

        // Get the positioning
        restPositioningMockMvc
            .perform(get(ENTITY_API_URL_ID, positioning.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(positioning.getId().intValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingPositioning() throws Exception {
        // Get the positioning
        restPositioningMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPositioning() throws Exception {
        // Initialize the database
        positioningRepository.saveAndFlush(positioning);

        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();

        // Update the positioning
        Positioning updatedPositioning = positioningRepository.findById(positioning.getId()).get();
        // Disconnect from session so that the updates on updatedPositioning are not directly saved in db
        em.detach(updatedPositioning);
        updatedPositioning.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restPositioningMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPositioning.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPositioning))
            )
            .andExpect(status().isOk());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
        Positioning testPositioning = positioningList.get(positioningList.size() - 1);
        assertThat(testPositioning.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testPositioning.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void putNonExistingPositioning() throws Exception {
        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();
        positioning.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                put(ENTITY_API_URL_ID, positioning.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPositioning() throws Exception {
        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();
        positioning.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPositioning() throws Exception {
        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();
        positioning.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positioning)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePositioningWithPatch() throws Exception {
        // Initialize the database
        positioningRepository.saveAndFlush(positioning);

        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();

        // Update the positioning using partial update
        Positioning partialUpdatedPositioning = new Positioning();
        partialUpdatedPositioning.setId(positioning.getId());

        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPositioning.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPositioning))
            )
            .andExpect(status().isOk());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
        Positioning testPositioning = positioningList.get(positioningList.size() - 1);
        assertThat(testPositioning.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testPositioning.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void fullUpdatePositioningWithPatch() throws Exception {
        // Initialize the database
        positioningRepository.saveAndFlush(positioning);

        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();

        // Update the positioning using partial update
        Positioning partialUpdatedPositioning = new Positioning();
        partialUpdatedPositioning.setId(positioning.getId());

        partialUpdatedPositioning.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPositioning.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPositioning))
            )
            .andExpect(status().isOk());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
        Positioning testPositioning = positioningList.get(positioningList.size() - 1);
        assertThat(testPositioning.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testPositioning.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingPositioning() throws Exception {
        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();
        positioning.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, positioning.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPositioning() throws Exception {
        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();
        positioning.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPositioning() throws Exception {
        int databaseSizeBeforeUpdate = positioningRepository.findAll().size();
        positioning.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(positioning))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Positioning in the database
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePositioning() throws Exception {
        // Initialize the database
        positioningRepository.saveAndFlush(positioning);

        int databaseSizeBeforeDelete = positioningRepository.findAll().size();

        // Delete the positioning
        restPositioningMockMvc
            .perform(delete(ENTITY_API_URL_ID, positioning.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Positioning> positioningList = positioningRepository.findAll();
        assertThat(positioningList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
