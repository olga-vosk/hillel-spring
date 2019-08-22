package hillel.spring.petclinic.review;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import org.springframework.data.annotation.Version;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer doctorId;
    private Integer medicalRecordId;

    private Integer serviceStars;
    private Integer equipmentStars;
    private Integer qualificationStars;
    private Integer treatmentResultsStars;
    private Integer generalStars;
    private String comment;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @Version
    private Integer version;

    public Optional<Integer> getServiceStars() {
        return Optional.ofNullable(serviceStars);
    }

    public Optional<Integer> getEquipmentStars() {
        return Optional.ofNullable(equipmentStars);
    }

    public Optional<Integer> getQualificationStars() {
        return Optional.ofNullable(qualificationStars);
    }

    public Optional<Integer> getTreatmentResultsStars() {
        return Optional.ofNullable(treatmentResultsStars);
    }

    public Optional<Integer> getGeneralStars() {
        return Optional.ofNullable(generalStars);
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }
}
