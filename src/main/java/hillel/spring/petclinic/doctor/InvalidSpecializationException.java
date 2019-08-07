package hillel.spring.petclinic.doctor;


public class InvalidSpecializationException extends RuntimeException {
    public InvalidSpecializationException(String wrongSpecialization) {
        super(" Invalid specialization " + wrongSpecialization);
    }
}
