package hillel.spring.petclinic.doctor.dto;

import hillel.spring.petclinic.doctor.ValidSpecialization;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DoctorInputDto {
    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final List<@ValidSpecialization String> specialization;

}
