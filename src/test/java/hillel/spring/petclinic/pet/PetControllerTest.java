package hillel.spring.petclinic.pet;


import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    public void cleanup(){
        repository.deleteAll();
    }

    private void init(){
        repository.create(new Pet(1, "Tom", "Cat", 2, new Owner("Vasya")));
        repository.create(new Pet(2, "Jerry", "Mouse", 1, new Owner("Petya")));

    }

    @Test
    public void shouldCreatePet() throws Exception{
        mockMvc.perform(post("/pets")
                .contentType("application/json")
                .content(fromResource("petclinic/pet/create-pet.json"))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/pets/3"));
        Assertions.assertThat(repository.findById(3).isPresent());
    }

    @Test
    public void shouldUpdateTom() throws Exception{
        init();

        mockMvc.perform(put("pets/{id}", 1)
        .contentType("application/json")
                .content(fromResource("petclinic/pet/update-pet.json")))
        .andExpect(status().isOk());

        Assertions.assertThat(repository.findById(1).get().getAge()).isEqualTo(3);
    }

    @Test
    public void shouldDeleteJerry() throws Exception{
        init();

        mockMvc.perform(delete("pets/{id}", 2))
                .andExpect(status().isNoContent());


    }

    @Test
    public void shouldFindAllPets() throws Exception{
        init();
        // лучше repository.create(new Pet(....))
        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(content().json(fromResource("petclinic/pet/all-pets.json")))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].owner.name", is("Vasya")))
        ;
        //.andExpect(MockMvcResultMatchers.jsonPath("$"))

    }

    @Test
    public void shouldReturnTom() throws Exception{
        init();

        mockMvc.perform(get("/pets").param("name", "Tom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Tom")));
    }


    @Test
    public void shouldReturnJerry() throws Exception{
        init();

        mockMvc.perform(get("/pets")
                .param("name", "Jerry")
                .param("age", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Jerry")))
                .andExpect(jsonPath("$[0].age", is(1))
                );
    }

    public String fromResource(String path){
        try {
            File file = ResourceUtils.getFile("classpath:" + path);
            return Files.readString(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}