package hillel.spring;

import org.springframework.stereotype.Repository;


import java.util.Map;


@Repository
class HelloGlossaryRepo {
    private static final Map<String, String> repo = Map.of("en", "Hello!",
                                                           "it", "Ciao!",
                                                           "fr", "Salut!");


    String findHello(String lang){
        lang = lang.toLowerCase();
        return repo.getOrDefault(lang, lang + " language not supported!");
    }

    String[] getLanguages() {
        return repo.keySet().toArray(new String[0]);
    }
}
