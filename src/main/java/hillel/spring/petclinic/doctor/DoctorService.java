
package hillel.spring.petclinic.doctor;


import hillel.spring.petclinic.pet.NoSuchPetException;
import hillel.spring.petclinic.pet.Pet;
import hillel.spring.petclinic.pet.PetService;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final PetService petService;

    public DoctorService(DoctorRepository doctorRepository,
                         PetService petService) {
        this.doctorRepository = doctorRepository;
        this.petService = petService;
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> findById(Integer id){
        return doctorRepository.findById(id);
    }

    public void update(Doctor doctor){
        if (doctorRepository.existsById(doctor.getId()))
            doctorRepository.save(doctor);
        else {
            throw new NoSuchDoctorException();
        }
    }

    public boolean delete(Integer id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public Page<Doctor> findAll(Optional<List<String>> specialization,
                                Optional<String> name,
                                Pageable pageable){
        if (specialization.isPresent() && name.isPresent()){
            return doctorRepository
                    .findBySpecializationInAndNameIgnoreCaseStartingWith(
                            specialization.get(), name.get(), pageable);
        }
        if (specialization.isPresent()){
            return doctorRepository.findBySpecializationIn(specialization.get(), pageable);
        }
        if (name.isPresent()){
            return doctorRepository.findByNameIgnoreCaseStartingWith(name.get(), pageable);
        }
        return doctorRepository.findAll(pageable);
    }


    private static <T> Set<T> asSet(T... elements) {
        Set<T> set = new HashSet<>( elements.length );
        java.util.Collections.addAll( set, elements );
        return set;
    }

    public Schedule findOrCreateSchedule(Doctor doctor, LocalDate date) {
        Schedule schedule = doctor.getScheduleToDate().get(date);
        if (schedule == null){
            schedule = new Schedule();
            doctor.getScheduleToDate().put(date, schedule);
        }
        return schedule;
    }

    public Schedule findOrCreateSchedule(Integer doctorId, LocalDate date) {
        val mayBeDoctor = findById(doctorId);
        Doctor doctor =  mayBeDoctor.orElseThrow(NoSuchDoctorException::new);
        return findOrCreateSchedule(doctor, date);
    }

    public void schedulePetToDoctor(Integer doctorId, LocalDate date, Integer hour, Integer petId){
        val mayBeDoctor = findById(doctorId);
        Doctor doctor =  mayBeDoctor.orElseThrow(NoSuchDoctorException::new);
        Schedule schedule = findOrCreateSchedule(doctor, date);
        Optional<Pet> maybePet = petService.findById(petId);
        if (maybePet.isPresent()) {
            putToSchedule(schedule, hour, petId);
            doctorRepository.save(doctor);
        } else {
            throw new NoSuchPetException();
        }
    }


    public void putToSchedule(Schedule schedule, Integer hour, Integer petId){
        if ( hour < 8 || hour > 8 + 8)
            throw new InvalidHourException(hour);
        if (schedule.getHourToPetId().containsKey(hour)) {
            throw new ScheduleHourAlreadyBusy(hour);
        } else {
            schedule.getHourToPetId().put(hour, petId);
        }
    }

    @Transactional
    public void moveSchedule(LocalDate date, Integer fromDoctorId, Integer toDoctorId) {
        Optional<Doctor> fromMaybeDoctor = doctorRepository.findById(fromDoctorId);
        if (fromMaybeDoctor.isEmpty())
            throw new NoSuchDoctorException(fromDoctorId);
        Optional<Doctor> toMaybeDoctor = doctorRepository.findById(toDoctorId);
        if (toMaybeDoctor.isEmpty())
            throw new NoSuchDoctorException(toDoctorId);
        Doctor fromDoctor = fromMaybeDoctor.get();
        Doctor toDoctor = toMaybeDoctor.get();

        Schedule scheduleFrom =  fromDoctor.getScheduleToDate().get(date);
        Schedule scheduleTo = findOrCreateSchedule(toDoctor, date);

        Optional<Integer> mayBeBusyHour = scheduleFrom.getHourToPetId()
                .keySet()
                .stream()
                .filter(hour ->scheduleTo.getHourToPetId().containsKey(hour))
                .findFirst();

        if (mayBeBusyHour.isPresent()){
            throw new ScheduleHourAlreadyBusy(mayBeBusyHour.get());
        }


        scheduleTo.getHourToPetId().putAll(scheduleFrom.getHourToPetId());
        scheduleFrom.getHourToPetId().clear();

        doctorRepository.save(toDoctor);
        doctorRepository.save(fromDoctor);
    }
}
