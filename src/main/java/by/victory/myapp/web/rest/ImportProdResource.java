package by.victory.myapp.web.rest;

import by.victory.myapp.domain.ImportProd;
import by.victory.myapp.repository.ImportProdRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link by.victory.myapp.domain.ImportProd}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ImportProdResource {

    private final Logger log = LoggerFactory.getLogger(ImportProdResource.class);

    private static final String ENTITY_NAME = "importProd";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImportProdRepository importProdRepository;

    public ImportProdResource(ImportProdRepository importProdRepository) {
        this.importProdRepository = importProdRepository;
    }

    /**
     * {@code POST  /import-prods} : Create a new importProd.
     *
     * @param importProd the importProd to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new importProd, or with status {@code 400 (Bad Request)} if the importProd has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/import-prods")
    public ResponseEntity<ImportProd> createImportProd(@Valid @RequestBody ImportProd importProd) throws URISyntaxException {
        log.debug("REST request to save ImportProd : {}", importProd);
        if (importProd.getId() != null) {
            throw new BadRequestAlertException("A new importProd cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ImportProd result = importProdRepository.save(importProd);
        return ResponseEntity
            .created(new URI("/api/import-prods/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /import-prods/:id} : Updates an existing importProd.
     *
     * @param id the id of the importProd to save.
     * @param importProd the importProd to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importProd,
     * or with status {@code 400 (Bad Request)} if the importProd is not valid,
     * or with status {@code 500 (Internal Server Error)} if the importProd couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/import-prods/{id}")
    public ResponseEntity<ImportProd> updateImportProd(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ImportProd importProd
    ) throws URISyntaxException {
        log.debug("REST request to update ImportProd : {}, {}", id, importProd);
        if (importProd.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importProd.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importProdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ImportProd result = importProdRepository.save(importProd);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importProd.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /import-prods/:id} : Partial updates given fields of an existing importProd, field will ignore if it is null
     *
     * @param id the id of the importProd to save.
     * @param importProd the importProd to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated importProd,
     * or with status {@code 400 (Bad Request)} if the importProd is not valid,
     * or with status {@code 404 (Not Found)} if the importProd is not found,
     * or with status {@code 500 (Internal Server Error)} if the importProd couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/import-prods/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ImportProd> partialUpdateImportProd(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ImportProd importProd
    ) throws URISyntaxException {
        log.debug("REST request to partial update ImportProd partially : {}, {}", id, importProd);
        if (importProd.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, importProd.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!importProdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ImportProd> result = importProdRepository
            .findById(importProd.getId())
            .map(existingImportProd -> {
                if (importProd.getArrivalDate() != null) {
                    existingImportProd.setArrivalDate(importProd.getArrivalDate());
                }

                return existingImportProd;
            })
            .map(importProdRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, importProd.getId().toString())
        );
    }

    /**
     * {@code GET  /import-prods} : get all the importProds.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of importProds in body.
     */
    @GetMapping("/import-prods")
    public ResponseEntity<List<ImportProd>> getAllImportProds(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ImportProds");
        Page<ImportProd> page = importProdRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /import-prods/:id} : get the "id" importProd.
     *
     * @param id the id of the importProd to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the importProd, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/import-prods/{id}")
    public ResponseEntity<ImportProd> getImportProd(@PathVariable Long id) {
        log.debug("REST request to get ImportProd : {}", id);
        Optional<ImportProd> importProd = importProdRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(importProd);
    }

    /**
     * {@code DELETE  /import-prods/:id} : delete the "id" importProd.
     *
     * @param id the id of the importProd to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/import-prods/{id}")
    public ResponseEntity<Void> deleteImportProd(@PathVariable Long id) {
        log.debug("REST request to delete ImportProd : {}", id);
        importProdRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
