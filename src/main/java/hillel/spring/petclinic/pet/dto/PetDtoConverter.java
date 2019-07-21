package hillel.spring.petclinic.pet.dto;

import hillel.spring.petclinic.pet.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface PetDtoConverter {

    @Mapping(target = "id", ignore = true)
    Pet toModel(PetInputDto dto);

}
