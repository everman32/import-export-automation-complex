package by.victory.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Grade.
 */
@Entity
@Table(name = "grade")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Grade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @OneToMany(mappedBy = "grade")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "trip", "grade" }, allowSetters = true)
    private Set<ImportProd> importProds = new HashSet<>();

    @OneToMany(mappedBy = "grade")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "trip", "grade" }, allowSetters = true)
    private Set<ExportProd> exportProds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Grade id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Grade description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ImportProd> getImportProds() {
        return this.importProds;
    }

    public void setImportProds(Set<ImportProd> importProds) {
        if (this.importProds != null) {
            this.importProds.forEach(i -> i.setGrade(null));
        }
        if (importProds != null) {
            importProds.forEach(i -> i.setGrade(this));
        }
        this.importProds = importProds;
    }

    public Grade importProds(Set<ImportProd> importProds) {
        this.setImportProds(importProds);
        return this;
    }

    public Grade addImportProd(ImportProd importProd) {
        this.importProds.add(importProd);
        importProd.setGrade(this);
        return this;
    }

    public Grade removeImportProd(ImportProd importProd) {
        this.importProds.remove(importProd);
        importProd.setGrade(null);
        return this;
    }

    public Set<ExportProd> getExportProds() {
        return this.exportProds;
    }

    public void setExportProds(Set<ExportProd> exportProds) {
        if (this.exportProds != null) {
            this.exportProds.forEach(i -> i.setGrade(null));
        }
        if (exportProds != null) {
            exportProds.forEach(i -> i.setGrade(this));
        }
        this.exportProds = exportProds;
    }

    public Grade exportProds(Set<ExportProd> exportProds) {
        this.setExportProds(exportProds);
        return this;
    }

    public Grade addExportProd(ExportProd exportProd) {
        this.exportProds.add(exportProd);
        exportProd.setGrade(this);
        return this;
    }

    public Grade removeExportProd(ExportProd exportProd) {
        this.exportProds.remove(exportProd);
        exportProd.setGrade(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Grade)) {
            return false;
        }
        return id != null && id.equals(((Grade) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Grade{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
