
package hillel.spring.petclinic.doctor;


import hillel.spring.petclinic.pet.NoSuchPetException;
import hillel.spring.petclinic.pet.Pet;
import hillel.spring.petclinic.pet.PetService;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DoctorService {
    private final Set<String> specializations;
    private final DoctorRepository doctorRepository;
    private final PetService petService;

    public DoctorService(@Value("${pet-clinic.doctors-specializations}") String[] specializations,
                         DoctorRepository doctorRepository,
                         PetService petService) {
        this.specializations = asSet(specializations);
        this.doctorRepository = doctorRepository;
        this.petService = petService;
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
        Optional<String> maybeInvalid = doctor.getSpecialization().stream()
                .filter(s -> !specializations.contains(s)).findFirst();

        if (maybeInvalid.isPresent())
            throw new InvalidSpecializationException(maybeInvalid.get(), specializations);
    }

    private static <T> Set<T> asSet(T... elements) {
        Set<T> set = new HashSet<>( elements.length );
        java.util.Collections.addAll( set, elements );
        return set;
    }


    public Schedule findSchedule(Integer doctorId, LocalDate date) {
        val mayBeDoctor = findById(doctorId);
        Doctor doctor =  mayBeDoctor.orElseThrow(NoSuchDoctorException::new);
        return doctor.findSchedule(date);
    }

    public void schedulePetToDoctor(Integer doctorId, LocalDate date, Integer hour, Integer petId){
        val mayBeDoctor = findById(doctorId);
        Doctor doctor =  mayBeDoctor.orElseThrow(NoSuchDoctorException::new);
        Schedule schedule = doctor.findSchedule(date);
        Optional<Pet> maybePet = petService.findById(petId);
        if (maybePet.isPresent()) {
            schedule.putToSchedule(hour, petId);
            doctorRepository.save(doctor);
        } else
            throw new NoSuchPetException();
    }
}
