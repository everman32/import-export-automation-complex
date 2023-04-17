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
 * A Trip.
 */
@Entity
@Table(name = "trip")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Trip implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "1")
    @Column(name = "authorized_capital", nullable = false)
    private Double authorizedCapital;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "threshold", nullable = false)
    private Double threshold;

    @OneToMany(mappedBy = "trip")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "statementType", "product", "positioning", "trip" }, allowSetters = true)
    private Set<Statement> statements = new HashSet<>();

    @ManyToOne
    private User user;

    @JsonIgnoreProperties(value = { "trip", "grade" }, allowSetters = true)
    @OneToOne(mappedBy = "trip")
    private ImportProd importProd;

    @JsonIgnoreProperties(value = { "trip", "grade" }, allowSetters = true)
    @OneToOne(mappedBy = "trip")
    private ExportProd exportProd;

    @ManyToOne
    @JsonIgnoreProperties(value = { "trips" }, allowSetters = true)
    private Transport transport;

    @ManyToOne
    @JsonIgnoreProperties(value = { "trips" }, allowSetters = true)
    private Driver driver;

    @ManyToOne
    @JsonIgnoreProperties(value = { "statements", "trips" }, allowSetters = true)
    private Positioning hubPositioning;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Trip id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAuthorizedCapital() {
        return this.authorizedCapital;
    }

    public Trip authorizedCapital(Double authorizedCapital) {
        this.setAuthorizedCapital(authorizedCapital);
        return this;
    }

    public void setAuthorizedCapital(Double authorizedCapital) {
        this.authorizedCapital = authorizedCapital;
    }

    public Double getThreshold() {
        return this.threshold;
    }

    public Trip threshold(Double threshold) {
        this.setThreshold(threshold);
        return this;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Set<Statement> getStatements() {
        return this.statements;
    }

    public void setStatements(Set<Statement> statements) {
        if (this.statements != null) {
            this.statements.forEach(i -> i.setTrip(null));
        }
        if (statements != null) {
            statements.forEach(i -> i.setTrip(this));
        }
        this.statements = statements;
    }

    public Trip statements(Set<Statement> statements) {
        this.setStatements(statements);
        return this;
    }

    public Trip addStatement(Statement statement) {
        this.statements.add(statement);
        statement.setTrip(this);
        return this;
    }

    public Trip removeStatement(Statement statement) {
        this.statements.remove(statement);
        statement.setTrip(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trip user(User user) {
        this.setUser(user);
        return this;
    }

    public ImportProd getImportProd() {
        return this.importProd;
    }

    public void setImportProd(ImportProd importProd) {
        if (this.importProd != null) {
            this.importProd.setTrip(null);
        }
        if (importProd != null) {
            importProd.setTrip(this);
        }
        this.importProd = importProd;
    }

    public Trip importProd(ImportProd importProd) {
        this.setImportProd(importProd);
        return this;
    }

    public ExportProd getExportProd() {
        return this.exportProd;
    }

    public void setExportProd(ExportProd exportProd) {
        if (this.exportProd != null) {
            this.exportProd.setTrip(null);
        }
        if (exportProd != null) {
            exportProd.setTrip(this);
        }
        this.exportProd = exportProd;
    }

    public Trip exportProd(ExportProd exportProd) {
        this.setExportProd(exportProd);
        return this;
    }

    public Transport getTransport() {
        return this.transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Trip transport(Transport transport) {
        this.setTransport(transport);
        return this;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Trip driver(Driver driver) {
        this.setDriver(driver);
        return this;
    }

    public Positioning getHubPositioning() {
        return this.hubPositioning;
    }

    public void setHubPositioning(Positioning positioning) {
        this.hubPositioning = positioning;
    }

    public Trip hubPositioning(Positioning positioning) {
        this.setHubPositioning(positioning);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trip)) {
            return false;
        }
        return id != null && id.equals(((Trip) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Trip{" +
            "id=" + getId() +
            ", authorizedCapital=" + getAuthorizedCapital() +
            ", threshold=" + getThreshold() +
            "}";
    }
}
