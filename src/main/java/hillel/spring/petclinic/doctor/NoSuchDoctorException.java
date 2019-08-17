package hillel.spring.petclinic.doctor;


public class NoSuchDoctorException extends RuntimeException {

    public NoSuchDoctorException() {
    }

    public NoSuchDoctorException(Integer id) {
        super("No dcotor with id " + id);
    }
}
