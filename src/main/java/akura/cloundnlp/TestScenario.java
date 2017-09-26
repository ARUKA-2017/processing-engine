package akura.cloundnlp;

import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
        System.out.println(gson.toJson(relationshipExtractor.sentenceSyntaxAnalysis(relationshipExtractor.sentenceTokenize("The phone arrived a day early in perfect condition. I was expecting scratches or dents but the phone looked brand new. Easy to configure and there are no problems with the battery life. I was sceptical about buying from this seller at first but I'm glad I did. I've had the phone for about 10 days now and still no problems"))));
    }
}
