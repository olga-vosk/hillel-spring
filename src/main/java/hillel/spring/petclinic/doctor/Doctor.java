package hillel.spring.petclinic.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    private Integer id;

    private String name;

    private String specialization;

    public void copy(Doctor anotherDoctor){
        setName(anotherDoctor.getName());
        setSpecialization(anotherDoctor.getSpecialization());
    }
}
