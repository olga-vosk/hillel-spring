package hillel.spring.petclinic.pet;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public Optional<Pet> findById(Integer id){
        return petRepository.findById(id);
    }

    public List<Pet> findAll(){
        return petRepository.findAll();
    }

    public void createPet(Pet pet) {
        petRepository.create(pet);
    }

    public void update(Pet pet){
        petRepository.update(pet);
    }

    public void delete(Integer id) {
         petRepository.delete(id);
    }

}
