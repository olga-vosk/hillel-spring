package hillel.spring.petclinic.doctor;


import java.util.Collection;

public class InvalidSpecializationException extends RuntimeException {
    public InvalidSpecializationException(String wrongSpecialization, Collection<String> allowedSpecializations) {
        super("Expected " + allowedSpecializations + ", but got " + wrongSpecialization);
    }
}
