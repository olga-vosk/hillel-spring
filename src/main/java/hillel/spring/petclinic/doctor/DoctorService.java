
package hillel.spring.petclinic.doctor;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.create(doctor);
    }
    
    public Optional<Doctor> findById(Integer id){
        return doctorRepository.findById(id);
    }

    public void update(Doctor doctor){
        doctorRepository.update(doctor);
    }

    public boolean delete(Integer id) {
        return doctorRepository.delete(id);
    }


    public Collection<Doctor> findAll(Predicate<Doctor> predicate){
        return doctorRepository.findAll()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

}
