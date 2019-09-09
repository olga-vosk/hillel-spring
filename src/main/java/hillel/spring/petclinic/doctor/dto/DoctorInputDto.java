package hillel.spring.petclinic.doctor.dto;

import hillel.spring.petclinic.doctor.ValidSpecialization;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class DoctorInputDto {

    @NotEmpty
    private final String name;

    @NotEmpty
    private final List<@ValidSpecialization String> specialization;

    @NotEmpty
    private final String diplomaNumber;

}
