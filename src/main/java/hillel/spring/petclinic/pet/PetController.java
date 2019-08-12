package hillel.spring.petclinic.pet;

import hillel.spring.petclinic.pet.dto.PetDtoConverter;
import hillel.spring.petclinic.pet.dto.PetInputDto;
import lombok.AllArgsConstructor;
import lombok.val;
import org.hibernate.StaleObjectStateException;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RestController

public class PetController {
    private final PetService petService;
    private final UriComponentsBuilder uriBuilder;

    private final PetDtoConverter dtoConverter =
     Mappers.getMapper(PetDtoConverter.class);


    public PetController(PetService petService,
                         @Value("${pet-clinic.host-name:localhost}") String hostname) {
        this.petService = petService;
        uriBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(hostname)
                .path("/pets/{id}");

    }

    @GetMapping("/pets/{id}")
    public Pet findById(@PathVariable Integer id){
        val mayBePet = petService.findById(id);

        return mayBePet.orElseThrow(PetNotFoundException::new);
    }

    @GetMapping("/pets")
    public List<Pet> findAll(@RequestParam Optional<String> name,
                             @RequestParam Optional<Integer> age){


        return petService.findAll(name, age);
    }

    private Predicate<Pet> filterByName(String name){
        return pet -> pet.getName().equals(name);
    }

    private Predicate<Pet> filterByAge(Integer age){
        return pet -> pet.getAge().equals(age);
    }

    @PostMapping("/pets")
    public ResponseEntity<?> createPet(@RequestBody PetInputDto dto){

        val created = petService.createPet(dtoConverter.toModel(dto));
        return ResponseEntity.created(uriBuilder.build(created.getId())).build();
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<?> updatePet(@RequestBody PetInputDto dto,
                                       @PathVariable Integer id){
        val pet = dtoConverter.toModel(dto, id);
        petService.update(pet);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pets/swap-wners/{firstPetId}/{secondPetId}")
    public void swapOwners(@PathVariable Integer firstPetId,
                           @PathVariable Integer secondPetId) {
        petService.swapOwners(firstPetId, secondPetId);
    }

    @PatchMapping("/pets/{id}")
    @Retryable(StaleObjectStateException.class)
    public void patchPet(@RequestBody PetInputDto dto,
                         @PathVariable Integer id){
        val pet = petService.findById(id).get();
        dtoConverter.update(pet, dto);
        petService.save(pet);
    }

    @DeleteMapping("pets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable Integer id){
        petService.delete(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void noSuchPet(NoSuchPetException ex){

    }
}


