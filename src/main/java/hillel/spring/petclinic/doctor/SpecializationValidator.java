package hillel.spring.petclinic.doctor;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

public class SpecializationValidator implements ConstraintValidator<ValidSpecialization, String>{
    private final Set<String> specializations;

    public SpecializationValidator(@Value("${pet-clinic.doctors-specializations}") String[] specializations) {
        this.specializations = asSet(specializations);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value!= null && specializations.contains(value);
    }
}
