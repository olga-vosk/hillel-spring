
package hillel.spring.petclinic.review;

import hillel.spring.petclinic.card.MedicalCardRepository;
import hillel.spring.petclinic.doctor.DoctorService;
import hillel.spring.petclinic.doctor.NoSuchDoctorException;
import hillel.spring.petclinic.review.dto.ReviewOutputDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final DoctorService doctorService;
    private final MedicalCardRepository medicalCardRepository;

    public Review save(Integer doctorId, Review toModel) {
        if (doctorId.compareTo(toModel.getDoctorId()) != 0){
            throw new DoctorIdMismatchException();
        }
        if (doctorService.findById(doctorId).isEmpty()){
            throw new NoSuchDoctorException();
        }
        if (medicalCardRepository.findByRecordsId(toModel.getMedicalRecordId()).isEmpty()) {
            throw new NoSuchMedicalRecordException();
        }
        checkStars(toModel.getQualificationStars());
        checkStars(toModel.getEquipmentStars());
        checkStars(toModel.getGeneralStars());
        checkStars(toModel.getServiceStars());
        checkStars(toModel.getServiceStars());

        return reviewRepository.save(toModel);
    }

    public ReviewOutputDto findReviews(Integer doctorId) {
        if (doctorService.findById(doctorId).isEmpty()){
            throw new NoSuchDoctorException();
        }
        List<Review> reviews = reviewRepository.findByDoctorId(doctorId);
        ReviewOutputDto reviewOutputDto = new ReviewOutputDto();
        reviewOutputDto.setAverageEquipmentStars(average(reviews, Review::getEquipmentStars));
        reviewOutputDto.setAverageQualificationStars(average(reviews, Review::getQualificationStars));
        reviewOutputDto.setAverageServiceStars(average(reviews, Review::getServiceStars));
        reviewOutputDto.setAverageTreatmentResultsStars(average(reviews, Review::getTreatmentResultsStars));
        reviewOutputDto.setAverageGeneralStars(average(reviews, Review::getGeneralStars));
        reviewOutputDto.setComments(reviews
                .stream()
                .map(s->new ReviewOutputDto.Comment(s.getCreationTime(), s.getComment().orElse("no comment")))
                .collect(Collectors.toList()));
        return reviewOutputDto;
    }

    public void update(Integer doctorId, Review review) {
        if (reviewRepository.existsById(review.getId())){
            save(doctorId, review);
        } else {
            throw new NoSuchReviewException();
        }

    }

    private Double average(List<Review> reviews, Function<Review, Optional<Integer>> field){
        ToIntFunction<Review> function = r->field.apply(r).orElse(-1);

        return reviews
                .stream()
                .mapToInt(function)
                .filter(x -> x > 0)
                .average()
                .orElse(0);
    }

    private void checkStars(Optional<Integer> maybeStars) {
        if (maybeStars.isPresent()) {
            int stars = maybeStars.get();
            if (stars < 1 || stars > 5)
                throw new InvalidRatingException();
        }
    }


}
