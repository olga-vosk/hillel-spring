package hillel.spring;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
public class HelloGlossaryController {
    private final HelloGlossaryService helloGlossaryService;

    @GetMapping("/greeting/{lang}")
    public String sayHello(@PathVariable String lang){
        return helloGlossaryService.findHello(lang);
    }
}
