package hillel.spring.petclinic.pet.dto;

import hillel.spring.petclinic.pet.Owner;
import lombok.Data;

@Data
public class PetInputDto {
    private final String name;
    private final String breed;
    private final Integer age;
    private final String owner;
}
