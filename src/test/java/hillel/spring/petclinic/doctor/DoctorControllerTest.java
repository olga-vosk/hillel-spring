package hillel.spring.petclinic.doctor;

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
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));

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
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));

        mockMvc.perform(get("/doctors/{id}", 2))
                .andExpect(jsonPath("$.name", is("Petr Petrov")))
                .andExpect(jsonPath("$.specialization", is("surgeon")));
    }

    @Test
    public void shouldNotFindDoctorById() throws Exception {
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));

        mockMvc.perform(get("/doctors/{id}", 3))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllSurgeons() throws Exception{
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.create(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.create(new Doctor(4, "Petr Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("specialization", "surgeon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].specialization", is("surgeon")))
                .andExpect(jsonPath("$[1].specialization", is("surgeon")));

    }

    @Test
    public void shouldFindAllPetrs() throws Exception {
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.create(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.create(new Doctor(4, "Petr Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("name", "Petr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", startsWith("Petr")))
                .andExpect(jsonPath("$[1].name", startsWith(("Petr"))));

    }

    @Test
    public void shouldFindPetrSurgeon() throws Exception {
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));
        repository.create(new Doctor(3, "Ivan Petrov", "surgeon"));
        repository.create(new Doctor(4, "Petr Ivanov", "veterinarian"));

        mockMvc.perform(get("/doctors")
                .param("specialization", "surgeon")
                .param("name", "Petr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Petr Petrov")))
                .andExpect(jsonPath("$[0].specialization", is("surgeon")));
    }

    @Test
    public void shouldUpdateDoctor() throws Exception{
        Integer id = repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian")).getId();

        mockMvc.perform(put("/doctors/{id}", id)
                        .contentType("application/json")
                        .content(fromResource("petclinic/doctor/update-doctor.json")))
                        .andExpect(status().isNoContent());

        assertThat(repository.findById(id).get().getName()).isEqualTo("Petr Petrov");
    }

    @Test
    public void shouldNotFindDoctorForUpdate() throws Exception{
       repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));

        mockMvc.perform(put("/doctors/{id}", 3000)
                .contentType("application/json")
                .content(fromResource("petclinic/doctor/update-id-doctor.json")))
                .andExpect(status().isNotFound());

    }

    
    @Test
    public void shouldDeleteDoctor() throws Exception{
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        Integer id = repository.create(new Doctor(2, "Petr Petrov", "surgeon")).getId();

        mockMvc.perform(delete("/doctors/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(id)).isEmpty();
    }


    @Test
    public void shouldNotDeleteDoctor() throws Exception{
        repository.create(new Doctor(1, "Ivan Ivanov", "veterinarian"));
        repository.create(new Doctor(2, "Petr Petrov", "surgeon"));

        mockMvc.perform(delete("/doctors/{id}", 1000))
                .andExpect(status().isNotFound());

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