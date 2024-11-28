package by.victory.myapp.web.rest;

import by.victory.myapp.domain.ExportProd;
import by.victory.myapp.repository.ExportProdRepository;
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
 * REST controller for managing {@link by.victory.myapp.domain.ExportProd}.
 */
@RestController
@RequestMapping("/api/export-prods")
@Transactional
public class ExportProdResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExportProdResource.class);

    private static final String ENTITY_NAME = "exportProd";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExportProdRepository exportProdRepository;

    public ExportProdResource(ExportProdRepository exportProdRepository) {
        this.exportProdRepository = exportProdRepository;
    }

    /**
     * {@code POST  /export-prods} : Create a new exportProd.
     *
     * @param exportProd the exportProd to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exportProd, or with status {@code 400 (Bad Request)} if the exportProd has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExportProd> createExportProd(@Valid @RequestBody ExportProd exportProd) throws URISyntaxException {
        LOG.debug("REST request to save ExportProd : {}", exportProd);
        if (exportProd.getId() != null) {
            throw new BadRequestAlertException("A new exportProd cannot already have an ID", ENTITY_NAME, "idexists");
        }
        exportProd = exportProdRepository.save(exportProd);
        return ResponseEntity.created(new URI("/api/export-prods/" + exportProd.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, exportProd.getId().toString()))
            .body(exportProd);
    }

    /**
     * {@code PUT  /export-prods/:id} : Updates an existing exportProd.
     *
     * @param id the id of the exportProd to save.
     * @param exportProd the exportProd to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exportProd,
     * or with status {@code 400 (Bad Request)} if the exportProd is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exportProd couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExportProd> updateExportProd(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExportProd exportProd
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExportProd : {}, {}", id, exportProd);
        if (exportProd.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exportProd.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exportProdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        exportProd = exportProdRepository.save(exportProd);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exportProd.getId().toString()))
            .body(exportProd);
    }

    /**
     * {@code PATCH  /export-prods/:id} : Partial updates given fields of an existing exportProd, field will ignore if it is null
     *
     * @param id the id of the exportProd to save.
     * @param exportProd the exportProd to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exportProd,
     * or with status {@code 400 (Bad Request)} if the exportProd is not valid,
     * or with status {@code 404 (Not Found)} if the exportProd is not found,
     * or with status {@code 500 (Internal Server Error)} if the exportProd couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExportProd> partialUpdateExportProd(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExportProd exportProd
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ExportProd partially : {}, {}", id, exportProd);
        if (exportProd.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exportProd.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!exportProdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExportProd> result = exportProdRepository
            .findById(exportProd.getId())
            .map(existingExportProd -> {
                if (exportProd.getDepartureDate() != null) {
                    existingExportProd.setDepartureDate(exportProd.getDepartureDate());
                }

                return existingExportProd;
            })
            .map(exportProdRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exportProd.getId().toString())
        );
    }

    /**
     * {@code GET  /export-prods} : get all the exportProds.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exportProds in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ExportProd>> getAllExportProds(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ExportProds");
        Page<ExportProd> page = exportProdRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /export-prods/:id} : get the "id" exportProd.
     *
     * @param id the id of the exportProd to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exportProd, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExportProd> getExportProd(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExportProd : {}", id);
        Optional<ExportProd> exportProd = exportProdRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(exportProd);
    }

    /**
     * {@code DELETE  /export-prods/:id} : delete the "id" exportProd.
     *
     * @param id the id of the exportProd to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExportProd(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExportProd : {}", id);
        exportProdRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
