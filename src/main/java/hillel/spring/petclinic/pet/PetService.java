package hillel.spring.petclinic.pet;

import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public Optional<Pet> findById(Integer id){
        return petRepository.findById(id);
    }

    public List<Pet> findAll(Optional<String> name, Optional<Integer> age){
        if (name.isPresent() && age.isPresent()){
            return petRepository.findByNameAndAge(name.get(), age.get());
        }
        if (name.isPresent()) {
            return petRepository.findByName(name.get());
        }
        if (age.isPresent()){
            return petRepository.findByAge(age.get());
        }
        return petRepository.findAll();
    }

    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }

    public void save(Pet pet){
        petRepository.save(pet);
    }

    public void delete(Integer id) {
         petRepository.deleteById(id);
    }

    @Transactional
    public void swapOwners(Integer firstPetId, Integer secondPetId)  {
        val firstPet = petRepository.findById(firstPetId).get();
        val secondPet = petRepository.findById(secondPetId).get();

        val firstOwner = firstPet.getOwner();
        firstPet.setOwner(secondPet.getOwner());
        secondPet.setOwner(firstOwner);

        petRepository.save(firstPet);
        petRepository.save(secondPet);
    }
}

