package hillel.spring.petclinic.doctor.dto;

import lombok.Data;

@Data
public class DoctorInputDto {
    private final String name;
    private final String specialization;

}
