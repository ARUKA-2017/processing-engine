package akura.cloundnlp;

import akura.utility.APIConnection;

import akura.cloundnlp.dtos.FinalEntityTagDto;
import akura.cloundnlp.dtos.OntologyMapDto;
import akura.cloundnlp.dtos.SyntaxDto;
import com.google.cloud.language.v1beta2.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.io.FileReader;
import java.io.IOException;

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
    private static LanguageServiceClient languageServiceClient;

    private static List<String> DOMAIN_TECHNOLOGY = new LinkedList<>();
    private static List<String> DOMAIN_COMPUTER = new LinkedList<>();
    private static List<String> DOMAIN_MOBILE = new LinkedList<>();
    private static List<String> DOMAIN_MEASUREMENT = new LinkedList<>();


    public static void main(String... args) throws Exception {
        languageServiceClient = APIConnection.provideLanguageServiceClient();
        JSONParser jsonParser = new JSONParser();
        JSONArray array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/SampleReviews.json"));
        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            String sampleText = jsonObject.get("reviewContent").toString();
//            domainTagMap = identifyDomains(sampleText, languageServiceClient);//not using
////            System.out.println();
//            System.out.println(sampleText);
//            System.out.println(domainTagMap);
////            System.out.println();

//            prioritizeEntities(identifySubDomains(analyseSyntax(sampleText, languageServiceClient)));
           // filterActualEntities(identifySubDomains(analyseSyntax(sampleText, languageServiceClient)));
            //final json
            ontologyMapDtos.add(constructJson(jsonObject, identifyReviewCategory(sampleText, languageServiceClient), analyseSyntax(sampleText, languageServiceClient)));
        }
        try (Writer writer = new FileWriter("Output.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(ontologyMapDtos, writer);
        }

        constructAvgScores(ontologyMapDtos.get(0).getFinalEntityTaggedList());
    }

    //identify entity priority list
    public static void prioritizeEntities(Map<Integer, List<String>> syntaxTagMap){

        TreeMap<String, Float> entityMap = new TreeMap<String, Float>();
        float salienceScore = 0;
        for (Map.Entry<Integer, List<String>> entityEntrySet: syntaxTagMap.entrySet()){
            if (entityEntrySet.getValue().size()>4)
                entityMap.put(entityEntrySet.getValue().toString(), Float.parseFloat(entityEntrySet.getValue().get(4)));
        }
        System.out.println(entriesSortedByValues(entityMap));
    }

    //filter and get the most needed entities by its domain
    public static void filterActualEntities(Map<Integer, List<String>> syntaxTagMap){
        Set<String> entitySet = new HashSet<>();
        for (Map.Entry<Integer, List<String>> entityEntrySet: syntaxTagMap.entrySet()){
            if (entityEntrySet.getValue().size()>5 && entityEntrySet.getValue().get(5).matches(".*CONSUMER.*")){
                System.out.println(entityEntrySet.getValue().get(5).toString());
                entitySet.add(entityEntrySet.getValue().get(0).toString());
            }
        }
        System.out.println(entitySet);
    }

    public static Map<Integer, List<String>> identifySubDomains(Map<Integer, List<String>> syntaxTagMap){
        Map<Integer, List<String>> newTagMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<String>> entrySet: syntaxTagMap.entrySet()){
            int key = entrySet.getKey();
            List<String> syntaxDetails = entrySet.getValue();
            if (syntaxDetails.size() > 5 && (syntaxDetails.get(5).equals("OTHER") || syntaxDetails.get(5).contains("UNKNOWN"))){
                //getting all unknown and other domain entities
                //find sub domains from manually added domains like - technology, computer, mobile, education, currency
//                String unknownEntity = syntaxDetails.get(0);
//                String domain = syntaxDetails.get(5);
//                syntaxDetails.remove(5);
//                syntaxDetails.add(understandShortWordConcept(unknownEntity, domain));
            }
            //map new tagmap with new sub domains
            newTagMap.put(key, syntaxDetails);
        }
        return newTagMap;
    }

    //not using only for testing purposes
    public static Map<String, List<String>> identifyDomains(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException {
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

    public static void analyseSentiment(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException {
        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        // Detects the sentiment of the text
        Sentiment sentiment = languageServiceClient.analyzeSentiment(doc).getDocumentSentiment();
    }
    //sort tree map by value
    static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     *
     * Entity extraction methods sequence
     *
     */

    /**
     * to identify the review category of a given review
     * @param text
     * @param languageServiceClient
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static Map<String, Float> identifyReviewCategory(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException {
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
                .setDocument(doc)
                .build();
        ClassifyTextResponse response = languageServiceClient.classifyText(request);
        Map<String, Float> categoryMap = new LinkedHashMap<>();
        for (ClassificationCategory category : response.getCategoriesList()) {
            categoryMap.put(category.getName().split("/")[1], category.getConfidence());
            break;
        }
        return categoryMap;
    }

    /**
     * analyse a given document(paragraph) and identify possible entities from google nlp according to their role play inside the paragraph and output and entity map with entity, category, sentiment and sailience
     * @param languageApi
     * @param doc
     * @return
     */
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

    /**
     * analyse a given document(paragraph) and output syntax tag map and final entity tag map
     * @param text
     * @param languageServiceClient
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static Map<String, Map<Integer, List<String>>> analyseSyntax(String text, LanguageServiceClient languageServiceClient) throws IOException, GeneralSecurityException {
        Map<String, Map<Integer, List<String>>> outputMap = new LinkedHashMap<>();
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        Map<String, List<String>> entitiesFound = analyseEntity(languageServiceClient, doc);//entities

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

            syntaxTagMap.put(++counter, tokenTags);
        }

        Map<String, String> mergedNouns = NounEntityExtractor.mergeNouns(syntaxTagMap);

        Map<Integer, List<String>> finalEntityTaggedMap = new LinkedHashMap<>();
        counter = 0;
        for(Map.Entry<String, List<String>> entityRow: entitiesFound.entrySet()){
            List<String> temporaryEntityDetailList = new LinkedList<>();
            String entity = entityRow.getValue().get(0);//.replace(" ", SEPARATOR);
            String organization = entityRow.getValue().get(1);
            String sentiment = ((entityRow.getValue().size() > 2)? (entityRow.getValue().get(2)): "");
            String sailience = ((entityRow.getValue().size() > 3)? (entityRow.getValue().get(3)): "");
            temporaryEntityDetailList.add(entity);
            temporaryEntityDetailList.add(sentiment);
            temporaryEntityDetailList.add(sailience);
            temporaryEntityDetailList.add(organization);
            for (Map.Entry<String, String> nounEntry: mergedNouns.entrySet()){
                if (nounEntry.getKey().toString().contains(entity)){
                    temporaryEntityDetailList.add(nounEntry.getKey());
                    temporaryEntityDetailList.add(nounEntry.getValue());
                    break;
                }
            }
            finalEntityTaggedMap.put(++counter, temporaryEntityDetailList);
        }
        outputMap.put("syntaxTagMap", syntaxTagMap);
        outputMap.put("finalEntityTaggedMap", finalEntityTaggedMap);

        return outputMap;

    }

    /**
     * construct json using the dto list
     * @param review
     * @param categoryMap
     * @param outputMap
     * @return
     */
    public static OntologyMapDto constructJson(JSONObject review, Map<String, Float> categoryMap, Map<String, Map<Integer, List<String>>> outputMap){
        ontologyMapDto = new OntologyMapDto();

        ontologyMapDto.setReviewId(review.get("review_id").toString());
        ontologyMapDto.setReview(review.get("reviewContent").toString());
        ontologyMapDto.setReviewRating(Float.parseFloat(review.get("rating").toString()));
        ontologyMapDto.setCategoryMap(categoryMap);

        List<SyntaxDto> syntaxDtos = new LinkedList<>();
        List<FinalEntityTagDto> finalEntityTagDtos = new LinkedList<>();
        for (Map.Entry<String, Map<Integer, List<String>>> entry : outputMap.entrySet()){
            if (entry.getKey().equals("syntaxTagMap")){
                for (Map.Entry<Integer, List<String>> subEntry: entry.getValue().entrySet()){
                    SyntaxDto syntaxDto = new SyntaxDto();
                    syntaxDto.setText(subEntry.getValue().get(0));
                    syntaxDto.setPos(subEntry.getValue().get(1));
                    syntaxDto.setLemma(subEntry.getValue().get(2));
                    syntaxDtos.add(syntaxDto);
                }
            } else if (entry.getKey().equals("finalEntityTaggedMap")){
                for (Map.Entry<Integer, List<String>> subEntry: entry.getValue().entrySet()){
                    FinalEntityTagDto finalEntityTagDto = new FinalEntityTagDto();
                    finalEntityTagDto.setText(subEntry.getValue().get(0));
                    finalEntityTagDto.setSentiment(Float.parseFloat(subEntry.getValue().get(1)));
                    finalEntityTagDto.setSalience(Float.parseFloat(subEntry.getValue().get(2)));
                    finalEntityTagDto.setCategory((subEntry.getValue().size()>3)?subEntry.getValue().get(3):"");
                    finalEntityTagDto.setNounCombination((subEntry.getValue().size()>4)?subEntry.getValue().get(4):"");

                    String text = subEntry.getValue().get(0);
                    String nounCombination = (subEntry.getValue().size()>4)?subEntry.getValue().get(4):"";
                    String nounCombinationCategory = (subEntry.getValue().size()>5)?subEntry.getValue().get(5):"";

                    if (nounCombinationCategory.equalsIgnoreCase("not found") && !text.equalsIgnoreCase(nounCombination)){
                        finalEntityTagDto.setNounCombination(text);
                        finalEntityTagDto.setNounCombinationCategory(APIConnection.understandShortWordConcept(text, "Not Found"));
                    } else {
                        finalEntityTagDto.setNounCombinationCategory((subEntry.getValue().size()>5)?subEntry.getValue().get(5):"");
                    }
                    finalEntityTagDtos.add(finalEntityTagDto);
                }
            }
        }

        ontologyMapDto.setSyntaxTagList(syntaxDtos);
        ontologyMapDto.setFinalEntityTaggedList(finalEntityTagDtos);
        return ontologyMapDto;
    }

    public static void constructAvgScores(List<FinalEntityTagDto> finalEntityTagDtos){
        List<FinalEntityTagDto> outputDtoList = new LinkedList<>();
        List<FinalEntityTagDto> removeList = new LinkedList<>();


        Iterator<FinalEntityTagDto> iterator = finalEntityTagDtos.iterator();
        while(iterator.hasNext()){
            FinalEntityTagDto finalEntityTagDto = iterator.next();

            FinalEntityTagDto temporaryDto = new FinalEntityTagDto();

            String entityName = finalEntityTagDto.getText();
            String entityCategory = finalEntityTagDto.getCategory();
            String nounCombination = finalEntityTagDto.getNounCombination();
            String nounCombinationCategory = finalEntityTagDto.getNounCombinationCategory();
            float sentiment = finalEntityTagDto.getSentiment();
            float salience = finalEntityTagDto.getSalience();

            iterator.remove();
            int counter = 1;

            String sum = String.valueOf(salience);
            while (iterator.hasNext()){
                FinalEntityTagDto finalEntityTagDto1 = iterator.next();
                if (entityName.equalsIgnoreCase(finalEntityTagDto1.getText()) || nounCombination.equalsIgnoreCase(finalEntityTagDto1.getNounCombination())){
                    counter++;
                    sum = sum + " " +String.valueOf(salience);
                    sentiment = (sentiment+finalEntityTagDto1.getSentiment());
                    salience = (salience+finalEntityTagDto1.getSalience());
                    iterator.remove();
                }
            }
            System.out.println(sum);

            temporaryDto.setText(entityName);
            temporaryDto.setCategory(entityCategory);
            temporaryDto.setSentiment(sentiment/counter);
            temporaryDto.setSalience(salience/counter);
            temporaryDto.setNounCombination(nounCombination);
            temporaryDto.setNounCombinationCategory(nounCombinationCategory);

            outputDtoList.add(temporaryDto);
            iterator = finalEntityTagDtos.iterator();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(outputDtoList));
    }
    /**
     * write output to a json document - output.json
     * @param ontologyMapDtos
     * @throws IOException
     */
    public void writeDocumentOutput(List<OntologyMapDto> ontologyMapDtos) throws IOException {
        try (Writer writer = new FileWriter("Output.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(ontologyMapDtos, writer);
        }
    }

    /**
     *
     * api endpoint method - test
     *
     */

    /**
     * Endpoint - extracted entity data
     * @param text
     * @return
     */
    public List<OntologyMapDto> extractEntityData(String text){
        try {
            languageServiceClient = APIConnection.provideLanguageServiceClient();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        JSONArray array = null;
        try {
            array = (JSONArray) jsonParser.parse(new FileReader("./src/main/java/akura/cloundnlp/sample_resources/SampleReviews.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<OntologyMapDto> ontologyMapDtos = new LinkedList<>();
        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.put("reviewContent", text);
            String sampleText = jsonObject.get("reviewContent").toString();

            try {
                ontologyMapDtos.add(constructJson(jsonObject, identifyReviewCategory(sampleText, languageServiceClient), analyseSyntax(sampleText, languageServiceClient)));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        return ontologyMapDtos;
    }
}