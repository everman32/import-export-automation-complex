package by.victory.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Statement.
 */
@Entity
@Table(name = "statement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Statement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @DecimalMin(value = "1")
    @Column(name = "transport_tariff", nullable = false)
    private Double transportTariff;

    @NotNull
    @DecimalMin(value = "1")
    @Column(name = "delivery_scope", nullable = false)
    private Double deliveryScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "statements" }, allowSetters = true)
    private StatementType statementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "statements", "productUnit" }, allowSetters = true)
    private Product product;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "statements", "trips" }, allowSetters = true)
    private Positioning positioning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "statements", "user", "importProd", "exportProd", "transport", "driver", "hubPositioning" },
        allowSetters = true
    )
    private Trip trip;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Statement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Statement name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTransportTariff() {
        return this.transportTariff;
    }

    public Statement transportTariff(Double transportTariff) {
        this.setTransportTariff(transportTariff);
        return this;
    }

    public void setTransportTariff(Double transportTariff) {
        this.transportTariff = transportTariff;
    }

    public Double getDeliveryScope() {
        return this.deliveryScope;
    }

    public Statement deliveryScope(Double deliveryScope) {
        this.setDeliveryScope(deliveryScope);
        return this;
    }

    public void setDeliveryScope(Double deliveryScope) {
        this.deliveryScope = deliveryScope;
    }

    public StatementType getStatementType() {
        return this.statementType;
    }

    public void setStatementType(StatementType statementType) {
        this.statementType = statementType;
    }

    public Statement statementType(StatementType statementType) {
        this.setStatementType(statementType);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Statement product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Positioning getPositioning() {
        return this.positioning;
    }

    public void setPositioning(Positioning positioning) {
        this.positioning = positioning;
    }

    public Statement positioning(Positioning positioning) {
        this.setPositioning(positioning);
        return this;
    }

    public Trip getTrip() {
        return this.trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Statement trip(Trip trip) {
        this.setTrip(trip);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Statement)) {
            return false;
        }
        return getId() != null && getId().equals(((Statement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Statement{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", transportTariff=" + getTransportTariff() +
            ", deliveryScope=" + getDeliveryScope() +
            "}";
    }
}
