package hillel.spring.petclinic.doctor;

import hillel.spring.petclinic.doctor.dto.DoctorDtoConverter;
import hillel.spring.petclinic.doctor.dto.DoctorInputDto;
import hillel.spring.petclinic.pet.NoSuchPetException;
import lombok.AllArgsConstructor;
import lombok.val;
import org.hibernate.StaleObjectStateException;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


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

    @PostMapping("/doctors/{doctorId}/schedule/{date}/{hour}")
    @Retryable(StaleObjectStateException.class)
    public ResponseEntity<?> schedulePetToDoctor(@PathVariable Integer doctorId,
                                                 @PathVariable
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                 @PathVariable Integer hour,
                                                 @RequestBody PetId petId){
        doctorService.schedulePetToDoctor(doctorId, date, hour, petId.getPetId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doctors/move-schedule/{fromDoctorId}/{toDoctorId}")
    public ResponseEntity<?> moveSchedule(@PathVariable Integer fromDoctorId,
                           @PathVariable Integer toDoctorId) {
        doctorService.moveSchedule(fromDoctorId, toDoctorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doctors/{id}")
    public Doctor findById(@PathVariable Integer id){
        val mayBeDoctor = doctorService.findById(id);

        return mayBeDoctor.orElseThrow(DoctorNotFoundException::new);
    }

    @GetMapping("/doctors/{doctorId}/schedule/{date}")
    public Schedule findSchedule(@PathVariable Integer doctorId,
                                 @PathVariable
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return doctorService.findOrCreateSchedule(doctorId, date);
    }

    @GetMapping("/doctors")
    public Collection<Doctor> findAll(@RequestParam Optional<List<String>> specialization,
                                      @RequestParam Optional<String> name){
        return doctorService.findAll(specialization, name);
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void invalidSpecialization(InvalidSpecializationException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void scheduleHourAlreadyBusy(ScheduleHourAlreadyBusy ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void invalidHour(InvalidHourException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noSuchPet(NoSuchPetException ex){
    }

}
