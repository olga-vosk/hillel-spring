package hillel.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRest {

    @GetMapping("/greeting")
    public String sayHello(){
        return "Hello!";
    }
}
