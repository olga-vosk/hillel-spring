package hillel.spring.petclinic.doctor;

import hillel.spring.petclinic.pet.Pet;
import hillel.spring.petclinic.pet.PetRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    DoctorRepository repository;

    @Autowired
    PetRepository petRepository;

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void shouldCreateDoctor() throws Exception{
        MockHttpServletResponse response = mockMvc.perform(post("/doctors")
                .contentType("application/json")
                .content(fromResource("petclinic/doctor/create-doctor.json"))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString("http://localhost/doctors/")))
                .andReturn().getResponse();

        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://localhost/doctors/", ""));

        assertThat(repository.findById(id)).isPresent();
    }

    @Test
    public void shouldFindAllDoctors() throws Exception {
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(content().json(fromResource("petclinic/doctor/all-doctors.json"), false))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Ivan Ivanov")))
                .andExpect(jsonPath("$[1].name", is("Petr Petrov")))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[1].id", notNullValue()));
    }


    @Test
    public void shouldFindDoctorById() throws Exception {
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        Integer id = repository.save(new Doctor(2, "Petr Petrov", "surgeon")).getId();

        mockMvc.perform(get("/doctors/{id}", id))
                .andExpect(jsonPath("$.name", is("Petr Petrov")))
                .andExpect(jsonPath("$.specialization[0]", is("surgeon")));
    }

    @Test
    public void shouldNotFindDoctorById() throws Exception {
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));

        mockMvc.perform(get("/doctors/{id}", 3))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllSurgeons() throws Exception{
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.save(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.save(new Doctor(4, "Petr Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("specialization", "surgeon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].specialization[0]", is("surgeon")))
                .andExpect(jsonPath("$[1].specialization[0]", is("surgeon")));

    }

    @Test
    public void shouldFindAllPetrs() throws Exception {
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.save(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.save(new Doctor(4, "Petr Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("name", "Petr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", startsWith("Petr")))
                .andExpect(jsonPath("$[1].name", startsWith(("Petr"))));

    }

    @Test
    public void shouldFindPetrSurgeon() throws Exception {
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.save(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.save(new Doctor(4, "Petr Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("specialization", "surgeon")
                .param("name", "Petr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Petr Petrov")))
                .andExpect(jsonPath("$[0].specialization[0]", is("surgeon")));
    }

    @Test
    public void shouldUpdateDoctor() throws Exception{
        Integer id = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();

        mockMvc.perform(put("/doctors/{id}", id)
                        .contentType("application/json")
                        .content(fromResource("petclinic/doctor/update-doctor.json")))
                        .andExpect(status().isNoContent());

        assertThat(repository.findById(id).get().getName()).isEqualTo("Petr Petrov");
    }

    @Test
    public void shouldNotFindDoctorForUpdate() throws Exception{
       repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));

        mockMvc.perform(put("/doctors/{id}", 3000)
                .contentType("application/json")
                .content(fromResource("petclinic/doctor/update-id-doctor.json")))
                .andExpect(status().isNotFound());

    }

    
    @Test
    public void shouldDeleteDoctor() throws Exception{
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        Integer id = repository.save(new Doctor(2, "Petr Petrov", "surgeon")).getId();

        mockMvc.perform(delete("/doctors/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(id)).isEmpty();
    }


    @Test
    public void shouldNotDeleteDoctor() throws Exception{
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));

        mockMvc.perform(delete("/doctors/{id}", 1000))
                .andExpect(status().isNotFound());

    }


    @Test
    public void shouldCheckSpecialization() throws Exception{

        mockMvc.perform(post("/doctors")
                .contentType("application/json")
                .content(fromResource("petclinic/doctor/wrong-spec-doctor.json"))
        )
                .andExpect(status().isBadRequest())
                ;

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void shouldFindByNameRegardlessRegister() throws Exception{
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.save(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.save(new Doctor(4, "pEtR Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("name", "PETR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldFindByNameAndSpecializationRegardlessRegister() throws Exception{
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.save(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.save(new Doctor(4, "pEtR Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("name", "PETR")
                .param("specialization", "surgeon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Petr Petrov")))
                .andExpect(jsonPath("$[0].specialization[0]", is("surgeon")));
    }

    @Test
    public void shouldFindByMultipleSpecializations() throws Exception{
        repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.save(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.save(new Doctor(3, "Ivan Petrov", "therapeutist"));
        repository.save(new Doctor(4, "pEtR Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("specialization", "surgeon", "veterinarian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/doctors")
                .param("specialization", "surgeon", "veterinarian")
                .param("name", "i"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Ivan Ivanov")))
                .andExpect(jsonPath("$[0].specialization[0]", is("veterinarian")));

    }

    @Test
    public void shouldSchedulePet() throws Exception{
        Integer doctorId = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();
        Integer petId = petRepository.save(new Pet(null, "Tom", "Cat", 2, "Vasya")).getId();

        mockMvc.perform(post("/doctors/{id}/schedule/2010-01-01/10", doctorId)
                .contentType("application/json")
                .content("{ \"petId\" : \"" + petId + "\"}")
        )
                .andExpect(status().isOk())
        ;

        Doctor doctor = repository.findById(doctorId).get();
        Schedule schedule = doctor.getScheduleToDate().get(LocalDate.of(2010,01,01));
        assertThat(schedule.getHourToPetId().size()).isEqualTo(1);
        assertThat(schedule.getHourToPetId().containsKey(10)).isTrue();
        assertThat(schedule.getHourToPetId().containsValue(petId)).isTrue();
    }


    @Test
    public void shouldFindSchedule() throws Exception{
        Integer doctorId = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();
        Integer petId = petRepository.save(new Pet(null, "Tom", "Cat", 2, "Vasya")).getId();
        Doctor doctor = repository.findById(doctorId).get();
        LocalDate localDate = LocalDate.of(2010, 01, 01);
        Schedule schedule = new Schedule();
        schedule.putToSchedule(10, petId);
        doctor.getScheduleToDate().put(localDate, schedule);
        repository.save(doctor);

        mockMvc.perform(get("/doctors/{id}/schedule/2010-01-01", doctorId))
                .andExpect(jsonPath("$.hourToPetId.10", is(petId)));
    }

    @Test
    public void shouldNotSchedulePetWithWrongDoctor() throws Exception{
        Integer doctorId = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();
        Integer petId = petRepository.save(new Pet(null, "Tom", "Cat", 2, "Vasya")).getId();

        mockMvc.perform(post("/doctors/{id}/schedule/2010-01-01/10", doctorId +100)
                .contentType("application/json")
                .content("{ \"petId\" : \"" + petId + "\"}")
        )
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void shouldNotSchedulePetWithWrongPet() throws Exception{
        Integer doctorId = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();
        Integer petId = petRepository.save(new Pet(null, "Tom", "Cat", 2, "Vasya")).getId();

        mockMvc.perform(post("/doctors/{id}/schedule/2010-01-01/10", doctorId )
                .contentType("application/json")
                .content("{ \"petId\" : \"" + petId +100 + "\"}")
        )
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    public void shouldNotSchedulePetWithWrongHour() throws Exception{
        Integer doctorId = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();
        Integer petId = petRepository.save(new Pet(null, "Tom", "Cat", 2, "Vasya")).getId();

        mockMvc.perform(post("/doctors/{id}/schedule/2010-01-01/1000", doctorId )
                .contentType("application/json")
                .content("{ \"petId\" : \"" + petId  + "\"}")
        )
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void shouldNotSchedulePetForAlreadyBusyHour() throws Exception{
        Integer doctorId = repository.save(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();
        Integer catId = petRepository.save(new Pet(null, "Tom", "Cat", 2, "Vasya")).getId();
        Integer mouseId = petRepository.save(new Pet(null, "Jerry", "Mouse", 1, "Vasya")).getId();

        mockMvc.perform(post("/doctors/{id}/schedule/2010-01-01/10", doctorId)
                .contentType("application/json")
                .content("{ \"petId\" : \"" + catId + "\"}")
        )
                .andExpect(status().isOk())
        ;

        mockMvc.perform(post("/doctors/{id}/schedule/2010-01-01/10", doctorId)
                .contentType("application/json")
                .content("{ \"petId\" : \"" + mouseId + "\"}")
        )
                .andExpect(status().isBadRequest())
        ;

        Doctor doctor = repository.findById(doctorId).get();
        Schedule schedule = doctor.getScheduleToDate().get(LocalDate.of(2010,01,01));
        assertThat(schedule.getHourToPetId().size()).isEqualTo(1);
        assertThat(schedule.getHourToPetId().containsKey(10)).isTrue();
        assertThat(schedule.getHourToPetId().containsValue(catId)).isTrue();

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