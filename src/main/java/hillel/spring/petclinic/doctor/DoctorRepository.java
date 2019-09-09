package hillel.spring.petclinic.doctor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {


    Page<Doctor> findBySpecializationInAndNameIgnoreCaseStartingWith(List<String> specialization,
                                                                     String name,
                                                                     Pageable pageable);


    Page<Doctor> findBySpecializationIn(List<String> specialization, Pageable pageable);


    Page<Doctor> findByNameIgnoreCaseStartingWith(String name, Pageable pageable);

}
