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


    public void putToSchedule(Integer hour, Integer petId){
        if ( hour < 8 || hour > 8 + 8)
            throw new InvalidHourException(hour);
        if (hourToPetId.containsKey(hour))
            throw new ScheduleHourAlreadyBusy(hour);
        else
            hourToPetId.put(hour, petId);
    }
}
