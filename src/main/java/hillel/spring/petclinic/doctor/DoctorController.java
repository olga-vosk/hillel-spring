package hillel.spring.petclinic.doctor;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;


@RestController
@AllArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody Doctor doctor){
        if (doctor.getId() != null) {
            return ResponseEntity.badRequest().build();
        } else {
            Doctor doctorCreated = doctorService.createDoctor(doctor);
            return ResponseEntity.created(URI.create("/doctors/" + doctorCreated.getId())).build();
        }
    }

    @GetMapping("/doctors/{id}")
    public Doctor findById(@PathVariable Integer id){
        val mayBeDoctor = doctorService.findById(id);

        return mayBeDoctor.orElseThrow(DoctorNotFoundException::new);
    }

    @GetMapping("/doctors")
    public Collection<Doctor> findBySpec(@RequestParam(required = false) String specialization, @RequestParam(required = false) String name){
        if (specialization != null) {
            return doctorService.findBySpec(specialization);
        } else if (name != null){
            return doctorService.findByName(name);
        } else {
            return doctorService.findAll();
        }
    }



    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@RequestBody Doctor doctor,
                                          @PathVariable Integer id){

        if (!doctor.getId().equals(id)){
            return ResponseEntity.badRequest().build();
        }
        try {
            doctorService.update(doctor);
            return ResponseEntity.noContent().build();
        } catch (NoSuchDoctorException ex){
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("doctors/{id}")
    public ResponseEntity deleteDoctor(@PathVariable Integer id){
        boolean deleted = doctorService.delete(id);
        return new ResponseEntity(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

}
