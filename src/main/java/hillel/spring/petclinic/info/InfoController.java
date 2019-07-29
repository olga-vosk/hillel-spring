package hillel.spring.petclinic.info;

import hillel.spring.petclinic.WorkingHoursConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
    private final String clinicName;
    private final WorkingHoursConfig workingHoursConfig;

    public InfoController(@Value("${pet-clinic.name}") String clinicName,
                          WorkingHoursConfig workingHoursConfig){
        this.clinicName = clinicName;
        this.workingHoursConfig = workingHoursConfig;
    }

    @GetMapping("/clinic-name")
    public String getName(){
        return clinicName;
    }

    @GetMapping("/working-time")
    public String getWorkingTime(){
        return workingHoursConfig.getStart() + " - " + workingHoursConfig.getEnd();
    }
}
