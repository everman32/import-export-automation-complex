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
 * A Transport.
 */
@Entity
@Table(name = "transport")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "brand", nullable = false)
    private String brand;

    @NotNull
    @Size(min = 2)
    @Column(name = "model", nullable = false)
    private String model;

    @NotNull
    @Size(min = 17, max = 17)
    @Column(name = "vin", length = 17, nullable = false, unique = true)
    private String vin;

    @OneToMany(mappedBy = "transport")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "statements", "user", "importProd", "exportProd", "transport", "driver", "hubPositioning" },
        allowSetters = true
    )
    private Set<Trip> trips = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return this.brand;
    }

    public Transport brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public Transport model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return this.vin;
    }

    public Transport vin(String vin) {
        this.setVin(vin);
        return this;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Set<Trip> getTrips() {
        return this.trips;
    }

    public void setTrips(Set<Trip> trips) {
        if (this.trips != null) {
            this.trips.forEach(i -> i.setTransport(null));
        }
        if (trips != null) {
            trips.forEach(i -> i.setTransport(this));
        }
        this.trips = trips;
    }

    public Transport trips(Set<Trip> trips) {
        this.setTrips(trips);
        return this;
    }

    public Transport addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setTransport(this);
        return this;
    }

    public Transport removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setTransport(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transport)) {
            return false;
        }
        return id != null && id.equals(((Transport) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transport{" +
            "id=" + getId() +
            ", brand='" + getBrand() + "'" +
            ", model='" + getModel() + "'" +
            ", vin='" + getVin() + "'" +
            "}";
    }
}
