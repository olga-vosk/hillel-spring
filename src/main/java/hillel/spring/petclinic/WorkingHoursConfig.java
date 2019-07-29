package hillel.spring.petclinic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("pet-clinic.working-hours")
public class WorkingHoursConfig {
   private String start;
   private String end;


}
