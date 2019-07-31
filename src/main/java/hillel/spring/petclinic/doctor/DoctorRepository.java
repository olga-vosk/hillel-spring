package hillel.spring.petclinic.doctor;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {


    List<Doctor> findBySpecializationInAndNameIgnoreCaseStartingWith(List<String> specialization, String name);


    List<Doctor> findBySpecializationIn(List<String> specialization);


    List<Doctor> findByNameIgnoreCaseStartingWith(String name);

}
