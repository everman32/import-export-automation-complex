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
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "1")
    @Column(name = "cost_per_piece", nullable = false)
    private Double costPerPiece;

    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    @JsonIgnoreProperties(value = { "statementType", "product", "positioning", "trip" }, allowSetters = true)
    private Set<Statement> statements = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private ProductUnit productUnit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCostPerPiece() {
        return this.costPerPiece;
    }

    public Product costPerPiece(Double costPerPiece) {
        this.setCostPerPiece(costPerPiece);
        return this;
    }

    public void setCostPerPiece(Double costPerPiece) {
        this.costPerPiece = costPerPiece;
    }

    public Set<Statement> getStatements() {
        return this.statements;
    }

    public void setStatements(Set<Statement> statements) {
        if (this.statements != null) {
            this.statements.forEach(i -> i.setProduct(null));
        }
        if (statements != null) {
            statements.forEach(i -> i.setProduct(this));
        }
        this.statements = statements;
    }

    public Product statements(Set<Statement> statements) {
        this.setStatements(statements);
        return this;
    }

    public Product addStatement(Statement statement) {
        this.statements.add(statement);
        statement.setProduct(this);
        return this;
    }

    public Product removeStatement(Statement statement) {
        this.statements.remove(statement);
        statement.setProduct(null);
        return this;
    }

    public ProductUnit getProductUnit() {
        return this.productUnit;
    }

    public void setProductUnit(ProductUnit productUnit) {
        this.productUnit = productUnit;
    }

    public Product productUnit(ProductUnit productUnit) {
        this.setProductUnit(productUnit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", costPerPiece=" + getCostPerPiece() +
            "}";
    }
}
