package hillel.spring.petclinic.pet;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PetRepository {
    private final Map<Integer, Pet> idToPet = new ConcurrentHashMap<>();


    public List<Pet> findAll(){
        return new ArrayList<>(idToPet.values());
    }

    public Optional<Pet> findById(Integer id){
        return Optional.ofNullable(idToPet.get(id));
    }

    public void create(Pet pet) {
        idToPet.put(pet.getId(), pet);
    }

    public void update(Pet pet) {
        idToPet.replace(pet.getId(), pet);
    }


    public void delete(Integer id) {
        idToPet.remove(id);
    }

    public void deleteAll() {
        idToPet.clear();
    }
}
