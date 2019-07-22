package hillel.spring.petclinic.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Pet {
    private Integer id;
    private String name;
    private String breed;
    private Integer age;
    private Owner owner;
}
