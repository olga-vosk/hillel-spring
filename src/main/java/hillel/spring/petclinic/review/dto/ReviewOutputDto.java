
package hillel.spring.petclinic.review.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewOutputDto {
    private Double averageServiceStars;
    private Double averageEquipmentStars;
    private Double averageQualificationStars;
    private Double averageTreatmentResultsStars;
    private Double averageGeneralStars;
    private List<Comment> comments;

    @Data
    @AllArgsConstructor
    public static class Comment {
        private LocalDateTime creationTime;
        private String comment;
    }

}

