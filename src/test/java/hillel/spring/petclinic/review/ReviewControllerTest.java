package hillel.spring.petclinic.review;

import hillel.spring.petclinic.TestRunner;
import hillel.spring.petclinic.card.MedicalCard;
import hillel.spring.petclinic.card.MedicalCardRepository;
import hillel.spring.petclinic.card.MedicalRecord;
import hillel.spring.petclinic.doctor.Doctor;
import hillel.spring.petclinic.doctor.DoctorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@TestRunner
public class ReviewControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    MedicalCardRepository medicalCardRepository;

    @Test
    public void shouldReadAndComputeAverageReviews()throws Exception{
        Integer doctorId = doctorRepository.save(new Doctor(null, "Ivan Ivanov", "veterinarian")).getId();
        Review review = new Review();
        review.setDoctorId(doctorId);
        review.setEquipmentStars(1);
        review.setGeneralStars(2);
        review.setQualificationStars(3);
        review.setServiceStars(4);
        review.setTreatmentResultsStars(5);
        review.setComment("some comment");
        reviewRepository.save(review);

        review = new Review();
        review.setDoctorId(doctorId);
        review.setEquipmentStars(1);
        review.setGeneralStars(2);
        review.setQualificationStars(3);
        review.setServiceStars(4);
        review.setTreatmentResultsStars(1);
        review.setComment("some comment");

        reviewRepository.save(review);


        MockHttpServletResponse response = mockMvc.perform(get("/doctors/{doctorId}/reviews",doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageEquipmentStars", is(1.0)))
                .andExpect(jsonPath("$.averageGeneralStars", is(2.0)))
                .andExpect(jsonPath("$.averageQualificationStars", is(3.0)))
                .andExpect(jsonPath("$.averageServiceStars", is(4.0)))
                .andExpect(jsonPath("$.averageTreatmentResultsStars", is(3.0)))
                .andExpect(jsonPath("$.comments" ,hasSize(2)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());
    }

    @Test
    public void shouldReadAndComputeAverageReviewsWithNullValues()throws Exception{
        Integer doctorId = doctorRepository.save(new Doctor(null, "Ivan Ivanov", "veterinarian")).getId();
        Review review = new Review();
        review.setDoctorId(doctorId);
        review.setEquipmentStars(1);
        review.setGeneralStars(2);
        review.setQualificationStars(3);
        review.setServiceStars(4);
        review.setTreatmentResultsStars(5);
        review.setComment("some comment 1");
        reviewRepository.save(review);

        review = new Review();
        review.setDoctorId(doctorId);
        review.setComment("some comment 2");
        reviewRepository.save(review);

        review = new Review();
        review.setDoctorId(doctorId);
        review.setComment("some comment 3");
        reviewRepository.save(review);



        MockHttpServletResponse response = mockMvc.perform(get("/doctors/{doctorId}/reviews",doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageEquipmentStars", is(1.0)))
                .andExpect(jsonPath("$.averageGeneralStars", is(2.0)))
                .andExpect(jsonPath("$.averageQualificationStars", is(3.0)))
                .andExpect(jsonPath("$.averageServiceStars", is(4.0)))
                .andExpect(jsonPath("$.averageTreatmentResultsStars", is(5.0)))
                .andExpect(jsonPath("$.comments" ,hasSize(3)))
                .andReturn().getResponse()  ;

        System.out.println(response.getContentAsString());

    }

    @Test
    public void shouldCreateReview() throws Exception{
        Integer doctorId = doctorRepository.save(new Doctor(null, "Ivan Ivanov", "veterinarian")).getId();

        MedicalCard medicalCard = new MedicalCard();
        medicalCard.setRecords(new ArrayList<>());
        medicalCard.getRecords().add(new MedicalRecord());
        medicalCardRepository.save(medicalCard);

        MockHttpServletResponse response = mockMvc.perform(post("/doctors/{doctorId}/reviews", doctorId)
                .contentType("application/json")
                .content(fromResource("petclinic/review/create-review.json")))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://localhost/doctors/"+doctorId+"/reviews/", ""));

        assertThat(reviewRepository.findById(id)).isPresent();

        System.out.println(response.getContentAsString());
    }

    @Test
    public void shouldNotCreateReviewWithoutDoctor() throws Exception{
         mockMvc.perform(post("/doctors/{doctorId}/reviews", 1)
                .contentType("application/json")
                .content(fromResource("petclinic/review/create-review.json")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateReview() throws Exception{
        Integer doctorId = doctorRepository.save(new Doctor(null, "Ivan Ivanov", "veterinarian")).getId();

        MedicalCard medicalCard = new MedicalCard();
        medicalCard.setRecords(new ArrayList<>());
        medicalCard.getRecords().add(new MedicalRecord());
        medicalCardRepository.save(medicalCard);

        Review review = new Review();
        review.setDoctorId(doctorId);
        review.setEquipmentStars(1);
        review.setGeneralStars(1);
        review.setQualificationStars(1);
        review.setServiceStars(1);
        review.setTreatmentResultsStars(1);
        review.setVersion(1);
        Integer reviewId = reviewRepository.save(review).getId();

        mockMvc.perform(
                put("/doctors/{doctorId}/reviews/{reviewId}", doctorId,reviewId)
                .contentType("application/json")
                .content("{\n" +
                        "  \"version\" : 1,\n" +
                        "  \"id\" : " + reviewId + ",\n" +
                        "  \"doctorId\" : " + doctorId + ",\n" +
                        "  \"medicalRecordId\" : 1,\n" +
                        "  \"serviceStars\" : 1,\n" +
                        "  \"equipmentStars\" : 2,\n" +
                        "  \"qualificationStars\" : 3,\n" +
                        "  \"treatmentResultsStars\" : 4,\n" +
                        "  \"generalStars\" : 5,\n" +
                        "  \"comment\" : \"some comment\"\n" +
                        "}\n"))
                .andExpect(status().isNoContent());
        Review reviewUpdated = reviewRepository.findById(reviewId).get();
        assertThat(reviewUpdated.getGeneralStars().get()).isEqualTo(5);
        assertThat(reviewUpdated.getComment().get()).isEqualToIgnoringCase("some comment");
    }

    public String fromResource(String path) {
        try {
            File file = ResourceUtils.getFile("classpath:" + path);
            return Files.readString(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
