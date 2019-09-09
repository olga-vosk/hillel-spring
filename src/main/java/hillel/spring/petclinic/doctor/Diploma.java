
package hillel.spring.petclinic.doctor;

import lombok.Data;

import javax.persistence.Embeddable;


@Data
@Embeddable
public class Diploma {

    private String university;
    private String specialization;
    private Integer graduationYear;

}
