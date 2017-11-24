package akura.cloundnlp;

import akura.cloundnlp.dtos.OntologyMapDto;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

/**
 * Test scenario class for test methods
 */
public class TestScenario {
    private static LanguageServiceClient languageServiceClient;
    static EntityExtractorInterface entityExtractor = new EntityExtractor();
    static SpecificationExtractorInterface specificationExtractor = new SpecificationExtractor();

    public static void main(String[] args) throws IOException, GeneralSecurityException, ParseException {
//        System.out.println(Pattern.compile("\\b"+"iphone 6s"+"\\b").matcher("I have an iPhone 6S and a Samsung Galaxy 6S").find());
//        System.out.println(Pattern.compile("\\b"+"iPhone 6"+"\\b").matcher("I have an iPhone 6S and a Samsung Galaxy 6S").find());
//        System.out.println(Pattern.compile("\\b"+"iPhone 6S"+"\\b").matcher("I have an iPhone 6S and a Samsung Galaxy 6S").find());
//        System.out.println(Pattern.compile("\\b"+"6S"+"\\b").matcher("I have an iPhone 6S and a Samsung Galaxy 6S").find());
//        languageServiceClient = APIConnection.provideLanguageServiceClient();
//        JSONParser jsonParser = new JSONParser();
//        JSONArray array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/SampleReviews.json"));
//        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
//        for (Object object : array) {
//            JSONObject jsonObject = (JSONObject) object;
//            String sampleText = jsonObject.get("reviewContent").toString();
//            //final json
//            RelationshipExtractor relationshipExtractor = new RelationshipExtractor();
//
//            List<String> newText = relationshipExtractor.replaceEntityInSentenceByITContext(
//                    relationshipExtractor.sentenceSyntaxAnalysis(
//                            relationshipExtractor.replaceEntityInSentences(
//                                    "IPhone 6S",
////                                    relationshipExtractor.sentenceTokenize("Earlier today, Apple unveiled two iPhone 6 models at its Special Event in Flint Center, Cupertino. The first device, the iPhone 6 comes with a 4.7-inch display and the second, the iPhone 6 Plus, comes with a 5.5-inch display. The iPhone 6 Plus has a 5.5-inch Retina HD display, with a pixel density of 401ppi and a resolution of 1920 x 1080 pixels. But there’s a lot more to the iPhone 6 Plus than just its screen. Here’s the full spec sheet of the iPhone 6 Plus."))
//                                    relationshipExtractor.sentenceTokenize("iPhone 6S and iPhone 6S Plus (stylized and marketed as iPhone 6s and iPhone 6s Plus) are smartphones designed, developed and marketed by Apple Inc. They were announced on September 9, 2015, at the Bill Graham Civic Auditorium in San Francisco by Apple CEO Tim Cook, with pre-orders beginning September 12 and official release on September 25, 2015. The iPhone 6S and 6S Plus were succeeded by the iPhone 7 and iPhone 7 Plus in September 2016. The iPhone 6S has a similar design to the 6 but updated hardware, including a strengthened chassis and upgraded system-on-chip, a 12-megapixel camera, improved fingerprint recognition sensor, and LTE Advanced support. The iPhone 6S also introduces a new hardware feature known as \"3D Touch\", which enables pressure-sensitive touch inputs. iPhone 6S had a mostly positive reception. While performance and camera quality were praised by most reviewers, the addition of 3D Touch was liked by one critic for the potential of entirely new interface interactions, but disliked by another critic for not providing users with an expected intuitive response before actually using the feature. The battery life was criticized, and one reviewer asserted that the phone's camera wasn't significantly better than the rest of the industry. The iPhone 6S set a new first-weekend sales record, selling 13 million models, up from 10 million for the iPhone 6 in the previous year. However, Apple saw its first-ever quarterly year-over-year decline in iPhone sales in the months after the launch, credited to a saturated smartphone market in Apple's biggest countries and a lack of iPhone purchases in developing countries."))
////                                    relationshipExtractor.sentenceTokenize("iPhone 6S and iPhone 6S Plus (stylized and marketed as iPhone 6s and iPhone 6s Plus) are smartphones designed, developed and marketed by Apple Inc. They were announced on September 9, 2015, at the Bill Graham Civic Auditorium in San Francisco by Apple CEO Tim Cook, with pre-orders beginning September 12 and official release on September 25, 2015. The iPhone 6S and 6S Plus were succeeded by the iPhone 7 and iPhone 7 Plus in September 2016. The iPhone 6S has a similar design to the 6 but updated hardware, including a strengthened chassis and upgraded system-on-chip, a 12-megapixel camera, improved fingerprint recognition sensor, and LTE Advanced support. The iPhone 6S also introduces a new hardware feature known as \"3D Touch\", which enables pressure-sensitive touch inputs."))
//                    )
//            );
//
//            String newString = "";
//            for (String newStr : newText){
//                newString += " "+newStr;
//            }
//            jsonObject.replace("reviewContent", newString);
//            jsonObject.put("mainEntity", "IPhone 6S");//should change before deployment
//
//            OntologyMapDto ontologyMapDto = entityExtractor.constructJson(jsonObject, entityExtractor.identifyReviewCategory(newString, languageServiceClient), entityExtractor.analyseSyntax(newString, languageServiceClient));
////            specificationExtractor.extractDomainsFromSentenceSyntax(ontologyMapDto.getFinalEntityTaggedList());
//            ontologyMapDtos.add(ontologyMapDto);
//
//            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ontologyMapDtos));
//        }
//
//        try (Writer writer = new FileWriter("Output.json")) {
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            gson.toJson(ontologyMapDtos, writer);
//        }

//        String text = "The iPhone 6 Plus has a 5.5-inch Retina HD display, with a pixel density of 401ppi and a resolution of 1920 x 1080 pixels.";
//        //relationships -----> has a, comes with a, has, comes with
//        if (text.contains("Retina HD display")){
//            String[] arr = text.split("Retina HD display");
//            if (arr[0].contains(" has a ")){
//                String[] relationshipSplit = arr[0].split("has a");
//                System.out.println(relationshipSplit[0].contains("iPhone 6 Plus"));
//            } else if (arr[0].contains(" comes with ")){
//
//            } else {
//                System.out.println(false);
//            }
//        }
//        JSONParser jsonParser = new JSONParser();
//        JSONArray array = null;
//        try {
//            array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/SampleReviews.json"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
//        for (Object object : array) {
//            JSONObject jsonObject = (JSONObject) object;
//            System.out.println(jsonObject.get("date"));
//            System.out.println();
//
//        }
//        Path path = FileSystems.getDefault().getPath("src/main/java/akura/cloundnlp/sample_resources/phone_dataset.json");
//        System.out.println(path.toAbsolutePath());

        List<OntologyMapDto> ooo = new LinkedList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dsds", ooo);

        System.out.println(new GsonBuilder().create().toJson(jsonObject));
    }
}
