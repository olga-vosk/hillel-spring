
package hillel.spring.petclinic.doctor;

public class ScheduleHourAlreadyBusy extends RuntimeException {
    public ScheduleHourAlreadyBusy(Integer hour) {
        super("Schedule hour already busy: " + hour);
    }
}
