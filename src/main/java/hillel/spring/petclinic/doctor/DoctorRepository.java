package hillel.spring.petclinic.doctor;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class DoctorRepository {
    private static final AtomicInteger currentId = new AtomicInteger(0);
    private static final Queue<Doctor> doctors = new ConcurrentLinkedQueue<>();

    {
        doctors.add(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        doctors.add(new Doctor(2, "Petr Petrov", "surgeon"));

        currentId.addAndGet(doctors.size());
    }


    public Doctor create(Doctor doctor) {
        doctor.setId(generateId());
        doctors.add(doctor);
        return doctor;
    }


    public Collection<Doctor> findAll(){
        return doctors;
    }

    public Optional<Doctor> findById(Integer id){
        return doctors.stream()
                .filter(it->it.getId().equals(id))
                .findFirst();
    }



    public void update(Doctor doctor) {
        Optional<Doctor> maybeDoctor = doctors.stream()
                .filter(d->doctor.getId().equals(d.getId()))
                .findFirst();
        if (maybeDoctor.isPresent()){
            Doctor doctorFound = maybeDoctor.get();
            doctorFound.copy(doctor);
        } else {
            throw new NoSuchDoctorException();
        }
    }



    public boolean delete(Integer id) {
        return doctors.removeIf(d->d.getId().equals( id));
    }


    public Collection<Doctor> findBySpec(String specialization) {
        return doctors.stream()
                .filter(d->d.getSpecialization().compareToIgnoreCase(specialization) == 0)
                .collect(Collectors.toList());
    }

    public Collection<Doctor> findByName(String name) {
        return doctors.stream()
                .filter(d->d.getName().startsWith(name))
                .collect(Collectors.toList());
    }

    private Integer generateId(){
        return currentId.incrementAndGet();
    }

}
