
package hillel.spring.petclinic.doctor;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class DoctorService {
    private static final Set<String> SPECIALIZATIONS = Set.of("surgeon", "veterinarian", "therapeutist");
    private final DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor) {
        checkSpecialization(doctor);
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> findById(Integer id){
        return doctorRepository.findById(id);
    }

    public void update(Doctor doctor){
        checkSpecialization(doctor);
        if (doctorRepository.existsById(doctor.getId()))
            doctorRepository.save(doctor);
        else
            throw new NoSuchDoctorException();
    }

    public boolean delete(Integer id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public Collection<Doctor> findAll(Optional<List<String>> specialization, Optional<String> name){
        if (specialization.isPresent() && name.isPresent()){
            return doctorRepository.findBySpecializationAndName(specialization.get(), name.get());
        }
        if (specialization.isPresent()){
            return doctorRepository.findBySpecialization(specialization.get());
        }
        if (name.isPresent()){
            return doctorRepository.findByName(name.get());
        }
        return doctorRepository.findAll();
    }

    private void checkSpecialization(Doctor doctor) {
        if (!SPECIALIZATIONS.contains(doctor.getSpecialization()))
            throw new InvalidSpecializationException();
    }

}
