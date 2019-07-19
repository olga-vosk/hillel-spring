
package hillel.spring.petclinic.doctor;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

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

    public Collection<Doctor> findAll(){
        return doctorRepository.findAll();
    }


    public void update(Doctor doctor){
        doctorRepository.update(doctor);
    }

    public boolean delete(Integer id) {
        return doctorRepository.delete(id);
    }

    public Collection<Doctor> findBySpec(String specialization) {
        return doctorRepository.findBySpec(specialization);
    }

    public Collection<Doctor> findByName(String name) {
        return doctorRepository.findByName(name);
    }

}
