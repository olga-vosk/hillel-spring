package hillel.spring.petclinic.doctor;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> specialization;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<LocalDate, Schedule> scheduleToDate;

    public Doctor(Integer id, String name, List<String> specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }


    public Doctor(Integer id, String name, String... specialization) {
        this.id = id;
        this.name = name;
        this.specialization = Arrays.asList(specialization);
    }


    public Schedule findSchedule(LocalDate date) {
        Schedule schedule = scheduleToDate.get(date);
        if (schedule == null){
             schedule = new Schedule();
             scheduleToDate.put(date, schedule);
        }
        return schedule;
    }
}
