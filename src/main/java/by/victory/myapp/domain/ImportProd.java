package by.victory.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ImportProd.
 */
@Entity
@Table(name = "import_prod")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ImportProd implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "arrivaldate", nullable = false)
    private Instant arrivaldate;

    @JsonIgnoreProperties(value = { "transport", "driver", "address", "product", "user" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Trip trip;

    @ManyToOne
    private Grade grade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ImportProd id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getArrivaldate() {
        return this.arrivaldate;
    }

    public ImportProd arrivaldate(Instant arrivaldate) {
        this.setArrivaldate(arrivaldate);
        return this;
    }

    public void setArrivaldate(Instant arrivaldate) {
        this.arrivaldate = arrivaldate;
    }

    public Trip getTrip() {
        return this.trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public ImportProd trip(Trip trip) {
        this.setTrip(trip);
        return this;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public ImportProd grade(Grade grade) {
        this.setGrade(grade);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportProd)) {
            return false;
        }
        return id != null && id.equals(((ImportProd) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImportProd{" +
            "id=" + getId() +
            ", arrivaldate='" + getArrivaldate() + "'" +
            "}";
    }
}
