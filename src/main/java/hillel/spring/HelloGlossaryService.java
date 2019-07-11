package hillel.spring;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
class HelloGlossaryService {
    private final HelloGlossaryRepo helloGlossaryRepo;
    private final Random random;

    public String findHello(String lang ){
        if ("random".compareToIgnoreCase(lang) == 0){
            lang = generateRandomLang();
        }
        return helloGlossaryRepo.findHello(lang);
    }

    public String generateRandomLang(){
        List<String> languages = helloGlossaryRepo.getLanguages();
        int randomIndex  = random.nextInt(languages.size());
        return languages.get(randomIndex);
    }
}
