package by.victory.myapp.web.rest;

import by.victory.myapp.domain.Statement;
import by.victory.myapp.repository.StatementRepository;
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
 * REST controller for managing {@link by.victory.myapp.domain.Statement}.
 */
@RestController
@RequestMapping("/api/statements")
@Transactional
public class StatementResource {

    private static final Logger LOG = LoggerFactory.getLogger(StatementResource.class);

    private static final String ENTITY_NAME = "statement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatementRepository statementRepository;

    public StatementResource(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    /**
     * {@code POST  /statements} : Create a new statement.
     *
     * @param statement the statement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statement, or with status {@code 400 (Bad Request)} if the statement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Statement> createStatement(@Valid @RequestBody Statement statement) throws URISyntaxException {
        LOG.debug("REST request to save Statement : {}", statement);
        if (statement.getId() != null) {
            throw new BadRequestAlertException("A new statement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        statement = statementRepository.save(statement);
        return ResponseEntity.created(new URI("/api/statements/" + statement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, statement.getId().toString()))
            .body(statement);
    }

    /**
     * {@code PUT  /statements/:id} : Updates an existing statement.
     *
     * @param id the id of the statement to save.
     * @param statement the statement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statement,
     * or with status {@code 400 (Bad Request)} if the statement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Statement> updateStatement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Statement statement
    ) throws URISyntaxException {
        LOG.debug("REST request to update Statement : {}, {}", id, statement);
        if (statement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        statement = statementRepository.save(statement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statement.getId().toString()))
            .body(statement);
    }

    /**
     * {@code PATCH  /statements/:id} : Partial updates given fields of an existing statement, field will ignore if it is null
     *
     * @param id the id of the statement to save.
     * @param statement the statement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statement,
     * or with status {@code 400 (Bad Request)} if the statement is not valid,
     * or with status {@code 404 (Not Found)} if the statement is not found,
     * or with status {@code 500 (Internal Server Error)} if the statement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Statement> partialUpdateStatement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Statement statement
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Statement partially : {}, {}", id, statement);
        if (statement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Statement> result = statementRepository
            .findById(statement.getId())
            .map(existingStatement -> {
                if (statement.getName() != null) {
                    existingStatement.setName(statement.getName());
                }
                if (statement.getTransportTariff() != null) {
                    existingStatement.setTransportTariff(statement.getTransportTariff());
                }
                if (statement.getDeliveryScope() != null) {
                    existingStatement.setDeliveryScope(statement.getDeliveryScope());
                }

                return existingStatement;
            })
            .map(statementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statement.getId().toString())
        );
    }

    /**
     * {@code GET  /statements} : get all the statements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Statement>> getAllStatements(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Statements");
        Page<Statement> page = statementRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /statements/:id} : get the "id" statement.
     *
     * @param id the id of the statement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Statement> getStatement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Statement : {}", id);
        Optional<Statement> statement = statementRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(statement);
    }

    /**
     * {@code DELETE  /statements/:id} : delete the "id" statement.
     *
     * @param id the id of the statement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Statement : {}", id);
        statementRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
