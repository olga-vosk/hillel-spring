package hillel.spring.petclinic.card;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Integer> {


    Optional<MedicalCard>  findByRecordsId(Integer medicalRecordId);
}
