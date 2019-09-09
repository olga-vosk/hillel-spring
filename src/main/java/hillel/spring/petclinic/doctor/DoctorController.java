package hillel.spring.petclinic.doctor;

import hillel.spring.petclinic.doctor.dto.DoctorDtoConverter;
import hillel.spring.petclinic.doctor.dto.DoctorInputDto;
import hillel.spring.petclinic.info.DoctorServiceConfig;
import hillel.spring.petclinic.pet.NoSuchPetException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.StaleObjectStateException;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;
    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .path("/doctors/{id}");

    private final DoctorDtoConverter dtoConverter = Mappers.getMapper(DoctorDtoConverter.class);

    private final RestTemplate restTemplate = new RestTemplate();

    private final DoctorServiceConfig doctorServiceConfig ;


    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@Valid @RequestBody DoctorInputDto dto){
        try {
            log.info("Calling diploma service");
            String url = doctorServiceConfig.getDiplomaUrl() + "/diploma/" + dto.getDiplomaNumber();
            log.debug("Dimploma service URL: {}", url);
            Diploma diploma = restTemplate.getForObject(url, Diploma.class);
            val created = doctorService.createDoctor(dtoConverter.toModel(dto), diploma);
            return ResponseEntity.created(uriBuilder.build(created.getId())).build();
        } catch (Exception e) {
            log.error("Call  to diploma service finished with exception", e);
        }
        return null;
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

    @PostMapping("/doctors/move-schedule/{date}/{fromDoctorId}/{toDoctorId}")
    public ResponseEntity<?> moveSchedule(@PathVariable
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                          @PathVariable Integer fromDoctorId,
                                          @PathVariable Integer toDoctorId) {
        doctorService.moveSchedule(date, fromDoctorId, toDoctorId);
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
    public Page<Doctor> findAll(@RequestParam Optional<List<String>> specialization,
                                @RequestParam Optional<String> name,
                                Pageable pageable){
        return doctorService.findAll(specialization, name, pageable);
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@Valid @RequestBody DoctorInputDto dto,
                                          @PathVariable Integer id){
        val doctor = dtoConverter.toModel(dto, id);
        doctorService.update(doctor);
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

