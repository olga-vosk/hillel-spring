package hillel.spring.petclinic.doctor;


import lombok.Data;

import org.springframework.data.annotation.Version;


import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Integer, Integer> hourToPetId = new HashMap<>();



}
