package hillel.spring;

import hillel.spring.petclinic.TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestRunner
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

}
