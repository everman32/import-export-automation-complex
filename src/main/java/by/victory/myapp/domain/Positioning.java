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
 * A Positioning.
 */
@Entity
@Table(name = "positioning")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class Positioning implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToMany(mappedBy = "positioning", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    @JsonIgnoreProperties(value = { "statementType", "product", "positioning", "trip" }, allowSetters = true)
    private Set<Statement> statements = new HashSet<>();

    @OneToMany(mappedBy = "hubPositioning", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    @JsonIgnoreProperties(
        value = { "statements", "user", "importProd", "exportProd", "transport", "driver", "hubPositioning" },
        allowSetters = true
    )
    private Set<Trip> trips = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Positioning id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Positioning latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Positioning longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<Statement> getStatements() {
        return this.statements;
    }

    public void setStatements(Set<Statement> statements) {
        if (this.statements != null) {
            this.statements.forEach(i -> i.setPositioning(null));
        }
        if (statements != null) {
            statements.forEach(i -> i.setPositioning(this));
        }
        this.statements = statements;
    }

    public Positioning statements(Set<Statement> statements) {
        this.setStatements(statements);
        return this;
    }

    public Positioning addStatement(Statement statement) {
        this.statements.add(statement);
        statement.setPositioning(this);
        return this;
    }

    public Positioning removeStatement(Statement statement) {
        this.statements.remove(statement);
        statement.setPositioning(null);
        return this;
    }

    public Set<Trip> getTrips() {
        return this.trips;
    }

    public void setTrips(Set<Trip> trips) {
        if (this.trips != null) {
            this.trips.forEach(i -> i.setHubPositioning(null));
        }
        if (trips != null) {
            trips.forEach(i -> i.setHubPositioning(this));
        }
        this.trips = trips;
    }

    public Positioning trips(Set<Trip> trips) {
        this.setTrips(trips);
        return this;
    }

    public Positioning addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setHubPositioning(this);
        return this;
    }

    public Positioning removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setHubPositioning(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Positioning)) {
            return false;
        }
        return id != null && id.equals(((Positioning) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Positioning{" +
            "id=" + getId() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            "}";
    }
}
