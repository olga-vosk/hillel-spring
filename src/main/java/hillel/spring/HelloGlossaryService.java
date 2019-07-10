package hillel.spring;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
class HelloGlossaryService {
    private final HelloGlossaryRepo helloGlossaryRepo;
    private final Random random;

    String findHello(String lang ){
        if ("random".compareToIgnoreCase(lang) == 0){
            lang = getRandomLang(random);
        }
        return helloGlossaryRepo.findHello(lang);
    }

    String getRandomLang(Random random){
        String[] languages = helloGlossaryRepo.getLanguages();
        int randomIndex  = random.nextInt(languages.length);
        return languages[randomIndex];
    }
}
