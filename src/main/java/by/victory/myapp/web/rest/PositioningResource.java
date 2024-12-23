package by.victory.myapp.web.rest;

import by.victory.myapp.domain.Positioning;
import by.victory.myapp.repository.PositioningRepository;
import by.victory.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.victory.myapp.domain.Positioning}.
 */
@RestController
@RequestMapping("/api/positionings")
@Transactional
public class PositioningResource {

    private static final Logger LOG = LoggerFactory.getLogger(PositioningResource.class);

    private static final String ENTITY_NAME = "positioning";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PositioningRepository positioningRepository;

    public PositioningResource(PositioningRepository positioningRepository) {
        this.positioningRepository = positioningRepository;
    }

    /**
     * {@code POST  /positionings} : Create a new positioning.
     *
     * @param positioning the positioning to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new positioning, or with status {@code 400 (Bad Request)} if the positioning has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Positioning> createPositioning(@Valid @RequestBody Positioning positioning) throws URISyntaxException {
        LOG.debug("REST request to save Positioning : {}", positioning);
        if (positioning.getId() != null) {
            throw new BadRequestAlertException("A new positioning cannot already have an ID", ENTITY_NAME, "idexists");
        }
        positioning = positioningRepository.save(positioning);
        return ResponseEntity.created(new URI("/api/positionings/" + positioning.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, positioning.getId().toString()))
            .body(positioning);
    }

    /**
     * {@code PUT  /positionings/:id} : Updates an existing positioning.
     *
     * @param id the id of the positioning to save.
     * @param positioning the positioning to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated positioning,
     * or with status {@code 400 (Bad Request)} if the positioning is not valid,
     * or with status {@code 500 (Internal Server Error)} if the positioning couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Positioning> updatePositioning(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Positioning positioning
    ) throws URISyntaxException {
        LOG.debug("REST request to update Positioning : {}, {}", id, positioning);
        if (positioning.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, positioning.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!positioningRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        positioning = positioningRepository.save(positioning);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, positioning.getId().toString()))
            .body(positioning);
    }

    /**
     * {@code PATCH  /positionings/:id} : Partial updates given fields of an existing positioning, field will ignore if it is null
     *
     * @param id the id of the positioning to save.
     * @param positioning the positioning to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated positioning,
     * or with status {@code 400 (Bad Request)} if the positioning is not valid,
     * or with status {@code 404 (Not Found)} if the positioning is not found,
     * or with status {@code 500 (Internal Server Error)} if the positioning couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Positioning> partialUpdatePositioning(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Positioning positioning
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Positioning partially : {}, {}", id, positioning);
        if (positioning.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, positioning.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!positioningRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Positioning> result = positioningRepository
            .findById(positioning.getId())
            .map(existingPositioning -> {
                if (positioning.getLatitude() != null) {
                    existingPositioning.setLatitude(positioning.getLatitude());
                }
                if (positioning.getLongitude() != null) {
                    existingPositioning.setLongitude(positioning.getLongitude());
                }

                return existingPositioning;
            })
            .map(positioningRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, positioning.getId().toString())
        );
    }

    /**
     * {@code GET  /positionings} : get all the positionings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of positionings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Positioning>> getAllPositionings(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Positionings");
        Page<Positioning> page = positioningRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /positionings/:id} : get the "id" positioning.
     *
     * @param id the id of the positioning to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the positioning, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Positioning> getPositioning(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Positioning : {}", id);
        Optional<Positioning> positioning = positioningRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(positioning);
    }

    /**
     * {@code DELETE  /positionings/:id} : delete the "id" positioning.
     *
     * @param id the id of the positioning to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePositioning(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Positioning : {}", id);
        positioningRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
