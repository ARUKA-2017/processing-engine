package akura.cloundnlp;

import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
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

//        RelationshipExtractor relationshipExtractor = new RelationshipExtractor();
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(
//                gson.toJson(
//                        relationshipExtractor.replaceEntityInSentenceByITContext(
//                                "IPhone 99999999",
//                                relationshipExtractor.sentenceSyntaxAnalysis(
//                                        relationshipExtractor.replaceEntityInSentences(
//                                                "IPhone 6",
//                                                relationshipExtractor.sentenceTokenize("A9 chip with M9 motion coprocessor Delivers outstanding overall performance for opening and running applications, flipping through menus, running home screens and more. Apple iOS 10 Everything you love is now even better. Express yourself in bold new ways in Messages. Find your route with beautifully redesigned Maps. Relive memories like never before in Photos. And use the power of Siri in more apps. 4GLTE speed Provides fast Web connection for downloading apps, streaming content and staying connected with social media. 4\\\" touch-screen Retina display with 1136 x 640 resolution and 326 ppi LED-backlit widescreen Multi-Touch display. 1136-by-640-pixel resolution at 326 ppi. Fingerprint-resistant oleophobic coating on front. 12MP iSight camera with Focus Pixels and True Tone flash Capture images while youre out and about. Also includes a front-facing 1.2MP FaceTime HD camera with Retina Flash. 32GB internal memory Provides plenty of storage space for your contacts, music, photos, apps and more. Cloud support lets you access your files anywhere Store your photos, videos, documents and other files on iCloud for secure access across multiple devices. Fees may apply. Touch ID fingerprint identity sensor Put your finger on the Home button, and just like that your iPhone unlocks. Your fingerprint can also approve purchases from iTunes or the App Store."))
//                                )
//                        )
//                        )
//                );

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
        JSONParser jsonParser = new JSONParser();
        JSONArray array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/phone_dataset.json"));

        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            String sampleText = jsonObject.get("FIELD1").toString();
            System.out.println(sampleText);
        }
        System.out.println(array.size());
    }
}
