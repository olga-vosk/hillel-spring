package hillel.spring.petclinic.review.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class ReviewInputDto {
    private Integer doctorId;
    private Integer medicalRecordId;
    private Integer serviceStars;
    private Integer equipmentStars;
    private Integer qualificationStars;
    private Integer treatmentResultsStars;
    private Integer generalStars;
    private String comment;

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
