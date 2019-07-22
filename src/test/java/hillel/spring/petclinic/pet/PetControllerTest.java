package hillel.spring.petclinic.pet;

import java.io.File;
import java.nio.file.Files;

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

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PetRepository repository;

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void shouldCreatePet() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/pets")
                .contentType("application/json")
                .content(fromResource("petclinic/pet/create-pet.json"))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString("http://localhost/pets/")))
                .andReturn().getResponse();

        Integer id = Integer.parseInt(response.getHeader("location")
                .replace("http://localhost/pets/", ""));

        assertThat(repository.findById(id)).isPresent();
    }

    @Test
    public void shouldUpdateTom() throws Exception {

        Integer id = repository.create(new Pet(null, "Tom", "Cat", 2, new Owner("Vasya"))).getId();

        mockMvc.perform(put("/pets/{id}", id)
                .contentType("application/json")
                .content(fromResource("petclinic/pet/update-pet.json")))
                .andExpect(status().isOk());

        assertThat(repository.findById(id).get().getAge()).isEqualTo(3);
    }

    @Test
    public void shouldDeleteJerry() throws Exception {
        repository.create(new Pet(null, "Tom", "Cat", 2, new Owner("Vasya")));
        repository.create(new Pet(null, "Jerry", "Mouse", 1, new Owner("Petya")));

        mockMvc.perform(delete("/pets/{id}", 2))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(2)).isEmpty();
    }

    @Test
    public void shouldFindAllPets() throws Exception {
        repository.create(new Pet(null, "Tom", "Cat", 2, new Owner("Vasya")));
        repository.create(new Pet(null, "Jerry", "Mouse", 1, new Owner("Petya")));

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(content().json(fromResource("petclinic/pet/all-pets.json"), false))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].owner.name", is("Vasya")))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[1].id", notNullValue()));
    }

    @Test
    public void shouldReturnTom() throws Exception {
        repository.create(new Pet(null, "Tom", "Cat", 2, new Owner("Vasya")));
        repository.create(new Pet(null, "Jerry", "Mouse", 1, new Owner("Petya")));

        mockMvc.perform(get("/pets").param("name", "Tom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Tom")));
    }

    @Test
    public void shouldReturnJerry() throws Exception {
        repository.create(new Pet(null, "Tom", "Cat", 2, new Owner("Vasya")));
        repository.create(new Pet(null, "Jerry", "Mouse", 1, new Owner("Petya")));

        mockMvc.perform(get("/pets")
                .param("name", "Jerry")
                .param("age", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Jerry")))
                .andExpect(jsonPath("$[0].age", is(1)));
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
