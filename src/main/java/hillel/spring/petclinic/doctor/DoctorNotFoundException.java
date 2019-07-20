package hillel.spring.petclinic.doctor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such doctor")
public class DoctorNotFoundException extends RuntimeException{
}
