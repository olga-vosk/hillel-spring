package hillel.spring;

import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
class HelloGlossaryRepo {
    private static final Map<String, String> repo = Map.of("en", "Hello!",
                                                           "it", "Ciao!",
                                                           "fr", "Salut!");


    String findHello(String lang){
        return repo.getOrDefault(lang.toLowerCase(), lang + " language not supported!");
    }

    List<String> getLanguages() {
        return new ArrayList<>(repo.keySet());
    }
}
