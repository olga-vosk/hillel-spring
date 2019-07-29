package hillel.spring.petclinic.doctor;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {


    @Query(" select doctor from Doctor as doctor" +
            " where doctor.specialization in :specialization and lower(doctor.name) like concat(lower(:name),'%') ")
    List<Doctor> findBySpecializationAndName(@Param("specialization") List<String> specialization, @Param("name") String name);

    @Query(" select doctor from Doctor as doctor where doctor.specialization in :specialization")
    List<Doctor> findBySpecialization(@Param("specialization") List<String> specialization);

    @Query("select doctor from Doctor as doctor where lower(doctor.name) like concat(lower(:name),'%')")
    List<Doctor> findByName(@Param("name") String name);

}
