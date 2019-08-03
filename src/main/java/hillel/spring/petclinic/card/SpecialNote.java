package hillel.spring.petclinic.card;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class SpecialNote {
    @Id
    @GeneratedValue
    private Integer id;
    private Boolean allergic;
    private String note;
}
