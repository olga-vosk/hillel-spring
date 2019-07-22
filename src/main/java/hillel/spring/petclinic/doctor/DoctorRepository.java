package hillel.spring.petclinic.doctor;


import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
public class DoctorRepository {
    private static final AtomicInteger currentId = new AtomicInteger(0);
    private static final Map<Integer, Doctor> doctors = new ConcurrentHashMap<>();


    public void deleteAll(){
        doctors.clear();
        currentId.set(0);
    }

    public Doctor create(Doctor doctor) {
        doctor.setId(generateId());
        //Doctor created = doctor.toBuilder().id(counter.incrementAndGet()).build();
        doctors.put(doctor.getId(), doctor);
        return doctor;
    }


    public Collection<Doctor> findAll(){
        return doctors.values();
    }

    public Optional<Doctor> findById(Integer id){
        return Optional.ofNullable(doctors.get(id));
    }



    public void update(Doctor doctor) {
        if (doctors.replace(doctor.getId(), doctor) == null)
            throw new NoSuchDoctorException();
    }



    public boolean delete(Integer id) {
        return doctors.remove(id) != null;
    }



    private Integer generateId(){
        return currentId.incrementAndGet();
    }

}
