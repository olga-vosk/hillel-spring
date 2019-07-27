package hillel.spring.petclinic.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
    private final String clinicName;
    private final String start;
    private final String end;

    public InfoController(@Value("${pet-clinic.name}") String clinicName,
                          @Value("${pet-clinic.working-hours.start}") String start,
                          @Value("${pet-clinic.working-hours.end}") String  end) {
        this.clinicName = clinicName;
        this.start = start;
        this.end = end;
    }

    @GetMapping("/clinic-name")
    public String getName(){
        return clinicName;
    }

    @GetMapping("/working-time")
    public String getWorkingTime(){
        return start + " - " + end;
    }
}
