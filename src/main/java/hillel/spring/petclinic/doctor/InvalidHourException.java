package hillel.spring.petclinic.doctor;


public class InvalidHourException extends RuntimeException {
    public InvalidHourException(Integer hour) {
        super("Invalid hour " + hour);
    }
}
