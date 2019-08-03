package hillel.spring.petclinic.card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Integer> {
}
