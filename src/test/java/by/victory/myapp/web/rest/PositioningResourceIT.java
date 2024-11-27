package by.victory.myapp.web.rest;

import static by.victory.myapp.domain.PositioningAsserts.*;
import static by.victory.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import by.victory.myapp.IntegrationTest;
import by.victory.myapp.domain.Positioning;
import by.victory.myapp.repository.PositioningRepository;
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
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PositioningRepository positioningRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPositioningMockMvc;

    private Positioning positioning;

    private Positioning insertedPositioning;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Positioning createEntity() {
        return new Positioning().latitude(DEFAULT_LATITUDE).longitude(DEFAULT_LONGITUDE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Positioning createUpdatedEntity() {
        return new Positioning().latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);
    }

    @BeforeEach
    public void initTest() {
        positioning = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPositioning != null) {
            positioningRepository.delete(insertedPositioning);
            insertedPositioning = null;
        }
    }

    @Test
    @Transactional
    void createPositioning() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Positioning
        var returnedPositioning = om.readValue(
            restPositioningMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(positioning)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Positioning.class
        );

        // Validate the Positioning in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPositioningUpdatableFieldsEquals(returnedPositioning, getPersistedPositioning(returnedPositioning));

        insertedPositioning = returnedPositioning;
    }

    @Test
    @Transactional
    void createPositioningWithExistingId() throws Exception {
        // Create the Positioning with an existing ID
        positioning.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(positioning)))
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        positioning.setLatitude(null);

        // Create the Positioning, which fails.

        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(positioning)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        positioning.setLongitude(null);

        // Create the Positioning, which fails.

        restPositioningMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(positioning)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPositionings() throws Exception {
        // Initialize the database
        insertedPositioning = positioningRepository.saveAndFlush(positioning);

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
        insertedPositioning = positioningRepository.saveAndFlush(positioning);

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
    void putExistingPositioning() throws Exception {
        // Initialize the database
        insertedPositioning = positioningRepository.saveAndFlush(positioning);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the positioning
        Positioning updatedPositioning = positioningRepository.findById(positioning.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPositioning are not directly saved in db
        em.detach(updatedPositioning);
        updatedPositioning.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restPositioningMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPositioning.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPositioning))
            )
            .andExpect(status().isOk());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPositioningToMatchAllProperties(updatedPositioning);
    }

    @Test
    @Transactional
    void putNonExistingPositioning() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        positioning.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                put(ENTITY_API_URL_ID, positioning.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPositioning() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        positioning.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPositioning() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        positioning.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(positioning)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePositioningWithPatch() throws Exception {
        // Initialize the database
        insertedPositioning = positioningRepository.saveAndFlush(positioning);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the positioning using partial update
        Positioning partialUpdatedPositioning = new Positioning();
        partialUpdatedPositioning.setId(positioning.getId());

        partialUpdatedPositioning.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPositioning.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPositioning))
            )
            .andExpect(status().isOk());

        // Validate the Positioning in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPositioningUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPositioning, positioning),
            getPersistedPositioning(positioning)
        );
    }

    @Test
    @Transactional
    void fullUpdatePositioningWithPatch() throws Exception {
        // Initialize the database
        insertedPositioning = positioningRepository.saveAndFlush(positioning);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the positioning using partial update
        Positioning partialUpdatedPositioning = new Positioning();
        partialUpdatedPositioning.setId(positioning.getId());

        partialUpdatedPositioning.latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPositioning.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPositioning))
            )
            .andExpect(status().isOk());

        // Validate the Positioning in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPositioningUpdatableFieldsEquals(partialUpdatedPositioning, getPersistedPositioning(partialUpdatedPositioning));
    }

    @Test
    @Transactional
    void patchNonExistingPositioning() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        positioning.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, positioning.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPositioning() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        positioning.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(positioning))
            )
            .andExpect(status().isBadRequest());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPositioning() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        positioning.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositioningMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(positioning)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Positioning in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePositioning() throws Exception {
        // Initialize the database
        insertedPositioning = positioningRepository.saveAndFlush(positioning);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the positioning
        restPositioningMockMvc
            .perform(delete(ENTITY_API_URL_ID, positioning.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return positioningRepository.count();
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

    protected Positioning getPersistedPositioning(Positioning positioning) {
        return positioningRepository.findById(positioning.getId()).orElseThrow();
    }

    protected void assertPersistedPositioningToMatchAllProperties(Positioning expectedPositioning) {
        assertPositioningAllPropertiesEquals(expectedPositioning, getPersistedPositioning(expectedPositioning));
    }

    protected void assertPersistedPositioningToMatchUpdatableProperties(Positioning expectedPositioning) {
        assertPositioningAllUpdatablePropertiesEquals(expectedPositioning, getPersistedPositioning(expectedPositioning));
    }
}
