
package hillel.spring.petclinic.doctor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorService {
    private final Set<String> specializations;
    private final DoctorRepository doctorRepository;

    public DoctorService(@Value("${pet-clinic.doctors-specializations}") String[] specializations,
                         DoctorRepository doctorRepository) {
        this.specializations = asSet(specializations);
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

    private static <T> Set<T> asSet(T... elements) {
        Set<T> set = new HashSet<>( elements.length );
        java.util.Collections.addAll( set, elements );
        return set;
    }


}
