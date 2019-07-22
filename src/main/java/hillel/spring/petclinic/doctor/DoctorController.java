package hillel.spring.petclinic.doctor;

import hillel.spring.petclinic.doctor.dto.DoctorDtoConverter;
import hillel.spring.petclinic.doctor.dto.DoctorInputDto;
import lombok.AllArgsConstructor;
import lombok.val;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


@RestController
@AllArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .path("/doctors/{id}");

    private final DoctorDtoConverter dtoConverter = Mappers.getMapper(DoctorDtoConverter.class);

    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody DoctorInputDto dto){
        val created = doctorService.createDoctor(dtoConverter.toModel(dto));
        return ResponseEntity.created(uriBuilder.build(created.getId())).build();
    }

    @GetMapping("/doctors/{id}")
    public Doctor findById(@PathVariable Integer id){
        val mayBeDoctor = doctorService.findById(id);

        return mayBeDoctor.orElseThrow(DoctorNotFoundException::new);
    }

    @GetMapping("/doctors")
    public Collection<Doctor> findAll(@RequestParam Optional<String> specialization,
                                      @RequestParam Optional<String> name){
        Optional<Predicate<Doctor>>  maybeSpecializationPredicate =  specialization.map(this::bySpecialization);
        Optional<Predicate<Doctor>>  maybeNamePredicate = name.map(this::byName);

        Predicate<Doctor> predicate = Stream.of(maybeSpecializationPredicate, maybeNamePredicate)
                .flatMap(Optional::stream)
                .reduce(Predicate::and)
                .orElse(pet -> true);

        return doctorService.findAll(predicate);
    }

    private Predicate<Doctor> bySpecialization(String specialization){
        return doctor -> doctor.getSpecialization().compareToIgnoreCase(specialization) == 0;
    }

    private Predicate<Doctor> byName(String name){
        return doctor -> doctor.getName().startsWith(name);
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorInputDto dto,
                                          @PathVariable Integer id){
        val pet = dtoConverter.toModel(dto, id);
        doctorService.update(pet);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("doctors/{id}")
    public ResponseEntity deleteDoctor(@PathVariable Integer id){
        boolean deleted = doctorService.delete(id);
        return new ResponseEntity(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noSuchDoctor(NoSuchDoctorException ex){
    }

}
