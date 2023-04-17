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
 * A StatementType.
 */
@Entity
@Table(name = "statement_type")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class StatementType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "statementType", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    @JsonIgnoreProperties(value = { "statementType", "product", "positioning", "trip" }, allowSetters = true)
    private Set<Statement> statements = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StatementType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public StatementType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Statement> getStatements() {
        return this.statements;
    }

    public void setStatements(Set<Statement> statements) {
        if (this.statements != null) {
            this.statements.forEach(i -> i.setStatementType(null));
        }
        if (statements != null) {
            statements.forEach(i -> i.setStatementType(this));
        }
        this.statements = statements;
    }

    public StatementType statements(Set<Statement> statements) {
        this.setStatements(statements);
        return this;
    }

    public StatementType addStatement(Statement statement) {
        this.statements.add(statement);
        statement.setStatementType(this);
        return this;
    }

    public StatementType removeStatement(Statement statement) {
        this.statements.remove(statement);
        statement.setStatementType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatementType)) {
            return false;
        }
        return id != null && id.equals(((StatementType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatementType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
