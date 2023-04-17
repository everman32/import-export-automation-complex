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
 * A Driver.
 */
@Entity
@Table(name = "driver")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Driver implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Size(min = 2)
    @Column(name = "patronymic")
    private String patronymic;

    @NotNull
    @Size(min = 2)
    @Column(name = "lastname", nullable = false)
    private String lastname;

    @NotNull
    @Size(min = 3)
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @NotNull
    @DecimalMin(value = "1")
    @Column(name = "experience", nullable = false)
    private Double experience;

    @OneToMany(mappedBy = "driver")
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

    public Driver id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public Driver firstname(String firstname) {
        this.setFirstname(firstname);
        return this;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPatronymic() {
        return this.patronymic;
    }

    public Driver patronymic(String patronymic) {
        this.setPatronymic(patronymic);
        return this;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getLastname() {
        return this.lastname;
    }

    public Driver lastname(String lastname) {
        this.setLastname(lastname);
        return this;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return this.phone;
    }

    public Driver phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getExperience() {
        return this.experience;
    }

    public Driver experience(Double experience) {
        this.setExperience(experience);
        return this;
    }

    public void setExperience(Double experience) {
        this.experience = experience;
    }

    public Set<Trip> getTrips() {
        return this.trips;
    }

    public void setTrips(Set<Trip> trips) {
        if (this.trips != null) {
            this.trips.forEach(i -> i.setDriver(null));
        }
        if (trips != null) {
            trips.forEach(i -> i.setDriver(this));
        }
        this.trips = trips;
    }

    public Driver trips(Set<Trip> trips) {
        this.setTrips(trips);
        return this;
    }

    public Driver addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setDriver(this);
        return this;
    }

    public Driver removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setDriver(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Driver)) {
            return false;
        }
        return id != null && id.equals(((Driver) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Driver{" +
            "id=" + getId() +
            ", firstname='" + getFirstname() + "'" +
            ", patronymic='" + getPatronymic() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", phone='" + getPhone() + "'" +
            ", experience=" + getExperience() +
            "}";
    }
}
