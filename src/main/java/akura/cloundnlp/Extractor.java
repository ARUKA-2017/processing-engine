package akura.cloundnlp;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.ObjectParser;
import com.google.cloud.language.v1beta2.*;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jena.atlas.json.io.parser.JSONP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * A snippet for Google Cloud Speech API showing how to analyze text message sentiment.
 */
public class Extractor {
    private static OntologyMapDto ontologyMapDto;
    private static Map<String, List<String>> domainTagMap = new LinkedHashMap<>();
    private static Map<Integer, List<String>> syntaxTagMap = new LinkedHashMap<>();
    private final static String SEPARATOR = "";

    private static List<String> DOMAIN_TECHNOLOGY = new LinkedList<>();
    private static List<String> DOMAIN_COMPUTER = new LinkedList<>();
    private static List<String> DOMAIN_MOBILE = new LinkedList<>();
    private static List<String> DOMAIN_MEASUREMENT = new LinkedList<>();


    public static void main(String... args) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONArray array = (JSONArray) jsonParser.parse(new FileReader("/Users/sameera/Documents/SLIIT/4th Year/2nd semester/cdap/processing-engine/src/main/java/akura/cloundnlp/SampleReviews.json"));
        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
        for (Object object: array){
            JSONObject jsonObject = (JSONObject) object;
            String sampleText = jsonObject.get("reviewContent").toString();
            domainTagMap = identifyDomains(sampleText);
            System.out.println();
            System.out.println(sampleText);
            System.out.println(domainTagMap);
            System.out.println();
            ontologyMapDtos.add(constructJson(jsonObject, identifyReviewCategory(sampleText), identifySubDomains(analyseSyntax(sampleText))));
        }

        Gson gson = new Gson();
        System.out.println(gson.toJson(ontologyMapDtos));

//        identifySubDomainsByMicrosoftApi("battery life");
//        System.out.println(identifySubDomainsByMicrosoftApi("US997","organization"));
    }
    //construct json through dto
    public static OntologyMapDto constructJson(JSONObject review, Map<String, Float> categoryMap, Map<Integer, List<String>> syntaxMap){
        ontologyMapDto = new OntologyMapDto();

        ontologyMapDto.setReviewId(review.get("review_id").toString());
        ontologyMapDto.setReview(review.get("reviewContent").toString());
        ontologyMapDto.setReviewRating(Float.parseFloat(review.get("rating").toString()));
        ontologyMapDto.setCategoryMap(categoryMap);
        ontologyMapDto.setData(syntaxMap);

        return ontologyMapDto;
    }
    //to identify the review category of a given review
    public static Map<String, Float> identifyReviewCategory(String text) throws IOException, GeneralSecurityException {
        GoogleCredential credential = authorize();
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
                .setDocument(doc)
                .build();
        // detect categories in the given text
        ClassifyTextResponse response = languageServiceClient.classifyText(request);
        Map<String, Float> categoryMap = new LinkedHashMap<>();//category, confidence
        for (ClassificationCategory category : response.getCategoriesList()) {
            categoryMap.put(category.getName(), category.getConfidence());
        }
        return categoryMap;
    }

    public static String identifySubDomainsByMicrosoftApi(String text, String organization) {
        String url = "http://concept.research.microsoft.com/api/Concept/ScoreByProb?instance=".concat(text).concat("&topK=1").replaceAll(" ","%20");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse httpResponse = client.execute(getRequest);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
//            System.out.println(EntityUtils.toString(httpEntity, "UTF-8"));

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(EntityUtils.toString(httpEntity, "UTF-8"));
            return jsonObject.keySet().iterator().next().toString();
        } catch (IOException e) {
            System.out.println(e);
            return organization;
        } catch (ParseException e) {
            System.out.println(e);
            return organization;
        } catch (NoSuchElementException e){
            System.out.println("Element not found!");
            return organization;
        }
    }

    public static Map<Integer, List<String>> identifySubDomains(Map<Integer, List<String>> syntaxTagMap){
        fillDomains();
        Map<Integer, List<String>> newTagMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<String>> entrySet: syntaxTagMap.entrySet()){
            int key = entrySet.getKey();
            List<String> syntaxDetails = entrySet.getValue();
            if (syntaxDetails.size() > 5 && (syntaxDetails.get(5).equals("OTHER") || syntaxDetails.get(5).contains("UNKNOWN"))){
                //getting all unknown and other domain entities
                //find sub domains from manually added domains like - technology, computer, mobile, education, currency
                String unknownEntity = syntaxDetails.get(0);

                String domain = syntaxDetails.get(5);
                syntaxDetails.remove(5);
                syntaxDetails.add(identifySubDomainsByMicrosoftApi(unknownEntity, domain));

//                for (String technologyDomain: DOMAIN_TECHNOLOGY){
//                    if (unknownEntity.contains(technologyDomain)){
//                        syntaxDetails.remove(5);
//                        syntaxDetails.add("TECHNOLOGY");
//                        break;
//                    }
//                }
//                for (String measurementDomain: DOMAIN_MEASUREMENT){
//                    if (unknownEntity.contains(measurementDomain)){
//                        syntaxDetails.remove(5);
//                        syntaxDetails.add("MEASUREMENT");
//                        break;
//                    }
//                }
//                for (String computerDomain: DOMAIN_COMPUTER){
//                    if (unknownEntity.contains(computerDomain)){
//                        syntaxDetails.remove(5);
//                        syntaxDetails.add("COMPUTER");
//                        break;
//                    }
//                }
//                for (String mobileDomain: DOMAIN_MOBILE){
//                    if (unknownEntity.contains(mobileDomain)){
//                        syntaxDetails.remove(5);
//                        syntaxDetails.add("MOBILE");
//                        break;
//                    }
//                }
            }
            //map new tagmap with new sub domains
            newTagMap.put(key, syntaxDetails);
        }
        return newTagMap;
    }

    public static void identifyFeatures(){

    }

    public static void identifyRelationships(){

    }

    public static void fillDomains(){
        //technology
        DOMAIN_TECHNOLOGY.add("camera");
        DOMAIN_TECHNOLOGY.add("lenses");
        DOMAIN_TECHNOLOGY.add("bluetooth");
        DOMAIN_TECHNOLOGY.add("headset");
        DOMAIN_TECHNOLOGY.add("touchpad");
        DOMAIN_TECHNOLOGY.add("front camera");
        DOMAIN_TECHNOLOGY.add("rear camera");
        DOMAIN_TECHNOLOGY.add("main camera");
        DOMAIN_TECHNOLOGY.add("cam");

        //computer
        DOMAIN_COMPUTER.add("laptop");
        DOMAIN_COMPUTER.add("pc");
        DOMAIN_COMPUTER.add("desktop");
        DOMAIN_COMPUTER.add("computer");
        DOMAIN_COMPUTER.add("optical mouse");
        DOMAIN_COMPUTER.add("mouse");
        DOMAIN_COMPUTER.add("keyboard");
        DOMAIN_COMPUTER.add("ram");
        DOMAIN_COMPUTER.add("ssd");

        //measurements
        DOMAIN_MEASUREMENT.add("weight");
        DOMAIN_MEASUREMENT.add("height");
        DOMAIN_MEASUREMENT.add("width");
        DOMAIN_MEASUREMENT.add("depth");
        DOMAIN_MEASUREMENT.add("price");
        DOMAIN_MEASUREMENT.add("pixel");
        DOMAIN_MEASUREMENT.add("performance");
        DOMAIN_MEASUREMENT.add("batterylife");

        //mobile
        DOMAIN_MOBILE.add("smartphone");
    }

    public static Map<String, List<String>> analyseEntity(LanguageServiceClient languageApi, Document doc) {
        Map<String, List<String>> entityList = new HashMap<>();

        AnalyzeEntitySentimentRequest request = AnalyzeEntitySentimentRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();
        AnalyzeEntitySentimentResponse response = languageApi.analyzeEntitySentiment(request);

        for(Entity entity : response.getEntitiesList())
        {
            List<String> detailList = new LinkedList<>();

            detailList.add(entity.getName());
            detailList.add(entity.getType().name());
            detailList.add(String.valueOf(entity.getSentiment().getScore()));
            detailList.add(String.valueOf(entity.getSalience()));

            entityList.put(UUID.randomUUID().toString(), detailList);
        }

        return entityList;
    }

    public static Map<Integer, List<String>> analyseSyntax(String text) throws IOException, GeneralSecurityException {
        GoogleCredential credential = authorize();
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        Map<String, List<String>> entitiesFound = analyseEntity(languageServiceClient, doc);//entities

        for(Map.Entry<String, List<String>> entityRow: entitiesFound.entrySet()){
            String entity = entityRow.getValue().get(0);

            if (text.contains(entity)){
                text = text.replaceAll(entity, entity.replace(" ", SEPARATOR));
            }
        }
        System.out.println(text);
        doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();

        AnalyzeSyntaxResponse response = languageServiceClient.analyzeSyntax(request);

        //text, postag, lemma
        Map<Integer, List<String>> syntaxTagMap = new LinkedHashMap<>();
        int counter = 0;

        for(Token token : response.getTokensList())
        {
            List<String> tokenTags = new LinkedList<>();
            tokenTags.add(token.getText().getContent());//text
            tokenTags.add(token.getPartOfSpeech().getTag().toString());//pos tag
            tokenTags.add(token.getLemma());//lemma

            for(Map.Entry<String, List<String>> entityRow: entitiesFound.entrySet()){
                String entity = entityRow.getValue().get(0).replace(" ", SEPARATOR);
                String organization = entityRow.getValue().get(1);

                if (entity.equals(token.getText().getContent())){
                    if (tokenTags.size() > 5 && tokenTags.get(5) != null) {
                        tokenTags.set(3, String.valueOf(
                                Float.parseFloat(tokenTags.get(3))+
                                Float.parseFloat(entityRow.getValue().get(2))
                        ));
                        tokenTags.set(4, String.valueOf(
                                Float.parseFloat(tokenTags.get(4))+
                                        Float.parseFloat(entityRow.getValue().get(3))
                        ));
                        continue;
                    }
                    tokenTags.add(entityRow.getValue().get(2));//sentiment
                    tokenTags.add(entityRow.getValue().get(3));//salience
                    tokenTags.add(organization);//domain
                }
            }
            syntaxTagMap.put(++counter, tokenTags);
        }
        return syntaxTagMap;

    }

    //authenticate google API
    private static GoogleCredential authorize() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        return credential;
    }

    public static Map<String, List<String>> identifyDomains(String text) throws IOException, GeneralSecurityException {
        GoogleCredential credential = authorize();
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();

        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

        Map<String, List<String>> entitiesFound = analyseEntity(languageServiceClient, doc);

        //main domain extraction map
        Map<String, List<String>> mainDomainExtraction = new LinkedHashMap<>();
        List<String> consumerGoodDomain = new LinkedList<>();
        List<String> workOfArtDomain = new LinkedList<>();
        List<String> organizationDomain = new LinkedList<>();
        List<String> locationDomain = new LinkedList<>();
        List<String> personDomain = new LinkedList<>();
        List<String> eventDomain = new LinkedList<>();
        List<String> unknownDomain = new LinkedList<>();
        List<String> otherDomain = new LinkedList<>();

        for(Map.Entry<String, List<String>> entityRow: entitiesFound.entrySet()){
            String entity = entityRow.getValue().get(0);
            String organization = entityRow.getValue().get(1);
            String sentiment = entityRow.getValue().get(2);
            String sailience = entityRow.getValue().get(3);
            System.out.println(entity+" "+organization);

            switch (organization){
                case "CONSUMER_GOOD": consumerGoodDomain.add(entity);
                    break;
                case "WORK_OF_ART": workOfArtDomain.add(entity);
                    break;
                case "ORGANIZATION": organizationDomain.add(entity);
                    break;
                case "LOCATION": locationDomain.add(entity);
                    break;
                case "PERSON": personDomain.add(entity);
                    break;
                case "EVENT": eventDomain.add(entity);
                    break;
                case "UNKNOWN": unknownDomain.add(entity);
                    break;
                case "OTHER": otherDomain.add(entity);
                    break;
            }
        }
        //updating domain map
        mainDomainExtraction.put("CONSUMER_GOOD", consumerGoodDomain);
        mainDomainExtraction.put("WORK_OF_ART", workOfArtDomain);
        mainDomainExtraction.put("ORGANIZATION", organizationDomain);
        mainDomainExtraction.put("LOCATION", locationDomain);
        mainDomainExtraction.put("PERSON", personDomain);
        mainDomainExtraction.put("EVENT", eventDomain);
        mainDomainExtraction.put("UNKNOWN", unknownDomain);
        mainDomainExtraction.put("OTHER", otherDomain);

        return mainDomainExtraction;
    }

    public static void analyseSentiment(String text) throws IOException, GeneralSecurityException {
        GoogleCredential credential = authorize();
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();

        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        // Detects the sentiment of the text
        Sentiment sentiment = languageServiceClient.analyzeSentiment(doc).getDocumentSentiment();

        System.out.println(" <------ GCP NLP Sentiment Analysis -----> ");

        System.out.println();
        System.out.println("Overall Score   : " + sentiment.getScore());
        System.out.println();

    }

}