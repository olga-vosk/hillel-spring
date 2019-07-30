
package hillel.spring.petclinic.doctor;



import org.mapstruct.ap.internal.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DoctorService {
    private final Set<String> specializations;
    private final DoctorRepository doctorRepository;

    public DoctorService(@Value("${pet-clinic.doctors-specializations}") String[] specializations,
                         DoctorRepository doctorRepository) {
        this.specializations = Collections.asSet(specializations);
        this.doctorRepository = doctorRepository;
    }

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
            return doctorRepository.findBySpecializationInAndNameIgnoreCaseStartingWith(specialization.get(), name.get());
        }
        if (specialization.isPresent()){
            return doctorRepository.findBySpecializationIn(specialization.get());
        }
        if (name.isPresent()){
            return doctorRepository.findByNameIgnoreCaseStartingWith(name.get());
        }
        return doctorRepository.findAll();
    }

    private void checkSpecialization(Doctor doctor) {
        if (!specializations.contains(doctor.getSpecialization()))
            throw new InvalidSpecializationException();
    }

}
