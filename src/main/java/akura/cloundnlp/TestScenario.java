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
        System.out.println(
                gson.toJson(
                        relationshipExtractor.sentenceSyntaxAnalysis(
                                relationshipExtractor.replaceEntityInSentences(
                                        "IPhone 6",
                                        relationshipExtractor.sentenceTokenize("Be careful, I ordered this phone and it wasn't an unlocked phone. It is setup on the US Reseller Flex Policy. It looked like it was purchased at Best Buy, \"sim free\". I could not activate it with an international SIM Card. Luckily I found someone with a US Verizon SIM card to install durning the activation process. Once it was activated with the Verizon card I was able to install my international Sim Card and everything worked"))
                                )
                        )
                );

//        System.out.println("This phone is dsab dsakldnsa dsalkd the phone dskadas".replaceAll("(?i)this phone","ccc").replaceAll("the phone", "xxx"));

    }
}
