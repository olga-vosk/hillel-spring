package hillel.spring.petclinic.doctor;


import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue
    private Integer id;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Integer, Integer> hourToPetId = new HashMap<>();



}
