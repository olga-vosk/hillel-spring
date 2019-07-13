package hillel.spring;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class HelloGlossaryServiceTest {

    @Test
    public void testRandomLangUniformDistribution() {
        HelloGlossaryRepo helloGlossaryRepo = new HelloGlossaryRepo();
        Random random = new Random();
        HelloGlossaryService helloGlossaryService = new HelloGlossaryService(helloGlossaryRepo, random);

        List<String> languages = helloGlossaryRepo.getLanguages();
        int totalCount = languages.size() * 10000;
        Map<String, Long> result =   Stream
                .generate(helloGlossaryService::generateRandomLang)
                .limit(totalCount)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                
        double probability = 1d/languages.size();
        System.out.println(result);
        result.values().forEach(v->System.out.format("probability expected %g, probaliclity actual %g \n",probability, (double)v/totalCount));

        result.values().forEach(v->assertEquals(probability, (double)v/totalCount, 0.009));

    }


}