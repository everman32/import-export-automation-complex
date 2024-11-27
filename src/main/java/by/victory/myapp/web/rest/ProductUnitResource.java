package by.victory.myapp.web.rest;

import by.victory.myapp.domain.ProductUnit;
import by.victory.myapp.repository.ProductUnitRepository;
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
 * REST controller for managing {@link by.victory.myapp.domain.ProductUnit}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProductUnitResource {

    private final Logger log = LoggerFactory.getLogger(ProductUnitResource.class);

    private static final String ENTITY_NAME = "productUnit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductUnitRepository productUnitRepository;

    public ProductUnitResource(ProductUnitRepository productUnitRepository) {
        this.productUnitRepository = productUnitRepository;
    }

    /**
     * {@code POST  /product-units} : Create a new productUnit.
     *
     * @param productUnit the productUnit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productUnit, or with status {@code 400 (Bad Request)} if the productUnit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-units")
    public ResponseEntity<ProductUnit> createProductUnit(@Valid @RequestBody ProductUnit productUnit) throws URISyntaxException {
        log.debug("REST request to save ProductUnit : {}", productUnit);
        if (productUnit.getId() != null) {
            throw new BadRequestAlertException("A new productUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductUnit result = productUnitRepository.save(productUnit);
        return ResponseEntity.created(new URI("/api/product-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-units/:id} : Updates an existing productUnit.
     *
     * @param id the id of the productUnit to save.
     * @param productUnit the productUnit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productUnit,
     * or with status {@code 400 (Bad Request)} if the productUnit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productUnit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-units/{id}")
    public ResponseEntity<ProductUnit> updateProductUnit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductUnit productUnit
    ) throws URISyntaxException {
        log.debug("REST request to update ProductUnit : {}, {}", id, productUnit);
        if (productUnit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productUnit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productUnitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductUnit result = productUnitRepository.save(productUnit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productUnit.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-units/:id} : Partial updates given fields of an existing productUnit, field will ignore if it is null
     *
     * @param id the id of the productUnit to save.
     * @param productUnit the productUnit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productUnit,
     * or with status {@code 400 (Bad Request)} if the productUnit is not valid,
     * or with status {@code 404 (Not Found)} if the productUnit is not found,
     * or with status {@code 500 (Internal Server Error)} if the productUnit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-units/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductUnit> partialUpdateProductUnit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductUnit productUnit
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductUnit partially : {}, {}", id, productUnit);
        if (productUnit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productUnit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productUnitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductUnit> result = productUnitRepository
            .findById(productUnit.getId())
            .map(existingProductUnit -> {
                if (productUnit.getName() != null) {
                    existingProductUnit.setName(productUnit.getName());
                }
                if (productUnit.getDescription() != null) {
                    existingProductUnit.setDescription(productUnit.getDescription());
                }

                return existingProductUnit;
            })
            .map(productUnitRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productUnit.getId().toString())
        );
    }

    /**
     * {@code GET  /product-units} : get all the productUnits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productUnits in body.
     */
    @GetMapping("/product-units")
    public ResponseEntity<List<ProductUnit>> getAllProductUnits(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ProductUnits");
        Page<ProductUnit> page = productUnitRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-units/:id} : get the "id" productUnit.
     *
     * @param id the id of the productUnit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productUnit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-units/{id}")
    public ResponseEntity<ProductUnit> getProductUnit(@PathVariable Long id) {
        log.debug("REST request to get ProductUnit : {}", id);
        Optional<ProductUnit> productUnit = productUnitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productUnit);
    }

    /**
     * {@code DELETE  /product-units/:id} : delete the "id" productUnit.
     *
     * @param id the id of the productUnit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-units/{id}")
    public ResponseEntity<Void> deleteProductUnit(@PathVariable Long id) {
        log.debug("REST request to delete ProductUnit : {}", id);
        productUnitRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
