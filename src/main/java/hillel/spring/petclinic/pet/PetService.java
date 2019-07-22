package hillel.spring.petclinic.pet;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public Optional<Pet> findById(Integer id){
        return petRepository.findById(id);
    }

    public List<Pet> findAll(Predicate<Pet> predicate){
        return petRepository.findAll()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public Pet createPet(Pet pet) {
        return petRepository.create(pet);
    }

    public void update(Pet pet){
        petRepository.update(pet);
    }

    public void delete(Integer id) {
         petRepository.delete(id);
    }

}
