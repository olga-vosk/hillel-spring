package hillel.spring.petclinic.doctor.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorInputDto {
    private final String name;
    private final List<String> specialization;

}
