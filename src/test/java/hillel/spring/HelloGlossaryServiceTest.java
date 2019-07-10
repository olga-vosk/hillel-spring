package hillel.spring;

import org.apache.el.stream.Stream;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * @author Воскобойникова О. А. <a href="mailto:olya@ibis.ua">olya@ibis.ua</a>
 */
public class HelloGlossaryServiceTest {

    @Test
    public void testRandomLangUniformDistribution() {
        HelloGlossaryRepo helloGlossaryRepo = new HelloGlossaryRepo();
        Random random = new Random();
        HelloGlossaryService helloGlossaryService = new HelloGlossaryService(helloGlossaryRepo, random);

        String[] languages = helloGlossaryRepo.getLanguages();
        Map<String, Double> result = new HashMap<>();

        int totalCount = languages.length * 10000;

        for (int i = 0; i < totalCount; i++){
            String lang = helloGlossaryService.getRandomLang(random);
            double count = 1 + result.getOrDefault(lang, 0d) ;
            result.put(lang, count);
        }
        double probability = 1d/languages.length ;
        System.out.println(result);
        result.values().forEach(v->System.out.format("probability expected %g, probaliclity actual %g \n",probability, v/totalCount));

        result.values().forEach(v->assertEquals(probability, v/totalCount, 0.009));

    }
}