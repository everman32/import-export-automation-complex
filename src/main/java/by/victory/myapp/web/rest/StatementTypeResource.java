package by.victory.myapp.web.rest;

import by.victory.myapp.domain.StatementType;
import by.victory.myapp.repository.StatementTypeRepository;
import by.victory.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link by.victory.myapp.domain.StatementType}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StatementTypeResource {

    private final Logger log = LoggerFactory.getLogger(StatementTypeResource.class);

    private static final String ENTITY_NAME = "statementType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatementTypeRepository statementTypeRepository;

    public StatementTypeResource(StatementTypeRepository statementTypeRepository) {
        this.statementTypeRepository = statementTypeRepository;
    }

    /**
     * {@code POST  /statement-types} : Create a new statementType.
     *
     * @param statementType the statementType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statementType, or with status {@code 400 (Bad Request)} if the statementType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/statement-types")
    public ResponseEntity<StatementType> createStatementType(@Valid @RequestBody StatementType statementType) throws URISyntaxException {
        log.debug("REST request to save StatementType : {}", statementType);
        if (statementType.getId() != null) {
            throw new BadRequestAlertException("A new statementType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StatementType result = statementTypeRepository.save(statementType);
        return ResponseEntity.created(new URI("/api/statement-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /statement-types/:id} : Updates an existing statementType.
     *
     * @param id the id of the statementType to save.
     * @param statementType the statementType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statementType,
     * or with status {@code 400 (Bad Request)} if the statementType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statementType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/statement-types/{id}")
    public ResponseEntity<StatementType> updateStatementType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StatementType statementType
    ) throws URISyntaxException {
        log.debug("REST request to update StatementType : {}, {}", id, statementType);
        if (statementType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statementType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statementTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StatementType result = statementTypeRepository.save(statementType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statementType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /statement-types/:id} : Partial updates given fields of an existing statementType, field will ignore if it is null
     *
     * @param id the id of the statementType to save.
     * @param statementType the statementType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statementType,
     * or with status {@code 400 (Bad Request)} if the statementType is not valid,
     * or with status {@code 404 (Not Found)} if the statementType is not found,
     * or with status {@code 500 (Internal Server Error)} if the statementType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/statement-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StatementType> partialUpdateStatementType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StatementType statementType
    ) throws URISyntaxException {
        log.debug("REST request to partial update StatementType partially : {}, {}", id, statementType);
        if (statementType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statementType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statementTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StatementType> result = statementTypeRepository
            .findById(statementType.getId())
            .map(existingStatementType -> {
                if (statementType.getName() != null) {
                    existingStatementType.setName(statementType.getName());
                }

                return existingStatementType;
            })
            .map(statementTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statementType.getId().toString())
        );
    }

    /**
     * {@code GET  /statement-types} : get all the statementTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statementTypes in body.
     */
    @GetMapping("/statement-types")
    public ResponseEntity<List<StatementType>> getAllStatementTypes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of StatementTypes");
        Page<StatementType> page = statementTypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /statement-types/:id} : get the "id" statementType.
     *
     * @param id the id of the statementType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statementType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/statement-types/{id}")
    public ResponseEntity<StatementType> getStatementType(@PathVariable Long id) {
        log.debug("REST request to get StatementType : {}", id);
        Optional<StatementType> statementType = statementTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(statementType);
    }

    /**
     * {@code DELETE  /statement-types/:id} : delete the "id" statementType.
     *
     * @param id the id of the statementType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/statement-types/{id}")
    public ResponseEntity<Void> deleteStatementType(@PathVariable Long id) {
        log.debug("REST request to delete StatementType : {}", id);
        statementTypeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
