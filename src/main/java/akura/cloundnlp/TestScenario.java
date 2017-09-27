package akura.cloundnlp;

import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sameera on 9/23/17.
 */
public class TestScenario {
    private static LanguageServiceClient languageServiceClient;

    public static void main(String[] args) throws IOException, GeneralSecurityException, ParseException {
//        languageServiceClient = APIConnection.provideLanguageServiceClient();
//        JSONParser jsonParser = new JSONParser();
//        JSONArray array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/SampleReviews.json"));
//        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
//        for (Object object : array) {
//            JSONObject jsonObject = (JSONObject) object;
//            String sampleText = jsonObject.get("reviewContent").toString();
//            //final json
//            ontologyMapDtos.add(constructJson(jsonObject, identifyReviewCategory(sampleText, languageServiceClient), analyseSyntax(sampleText, languageServiceClient)));
//        }
//        try (Writer writer = new FileWriter("Output.json")) {
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            gson.toJson(ontologyMapDtos, writer);
//        }

        RelationshipExtractor relationshipExtractor = new RelationshipExtractor();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(
                gson.toJson(
                        relationshipExtractor.replaceEntityInSentenceByITContext(
                                "IPhone 99999999",
                                relationshipExtractor.sentenceSyntaxAnalysis(
                                        relationshipExtractor.replaceEntityInSentences(
                                                "IPhone 6",
                                                relationshipExtractor.sentenceTokenize("IPhone 7 camera is awesome and it has superb sound system. It has a good front camera and it has bluetooth and it has great picture quality"))
                                )
                        )
                        )
                );

//        System.out.println("i have an iphone 4 and it has a superb camera and it is better than iphone 6 It and it".toLowerCase().replaceAll("and it|it", "00000"));

//        String[] splittedArray = "i have an iphone 4 and it has a superb camera and it is better than iphone 6".split("and it", 2);
//        Arrays.stream(splittedArray).forEach(item -> System.out.println(item));
//
//        List<Float> f = new LinkedList<>();
//        f.add(33.432f);
//        f.add(33.332f);
//        f.add(32.432f);
//        Collections.sort(f,Collections.reverseOrder());
//        f.stream().forEach(i -> System.out.println(i));
    }
}
