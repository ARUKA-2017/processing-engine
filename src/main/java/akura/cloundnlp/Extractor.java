package akura.cloundnlp;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.cloud.language.v1beta2.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * A snippet for Google Cloud Speech API showing how to analyze text message sentiment.
 */
public class Extractor {

    private static Map<String, List<String>> domainTagMap = new LinkedHashMap<>();
    private static Map<Integer, List<String>> syntaxTagMap = new LinkedHashMap<>();

    public static void main(String... args) throws Exception {
        domainTagMap = identifyDomains("This is an original Apple iPhone 7 Plus phone, that comes with original accessories. It's a US version, that is perfectly tuned for US mobile carriers.");
        syntaxTagMap = analyseSyntax("This is an original Apple iPhone 7 Plus phone, that comes with original accessories. It's a US version, that is perfectly tuned for US mobile carriers.");

    }



    public static Map<String, List<String>> identifyDomains(String text) throws IOException, GeneralSecurityException {
        GoogleCredential credential = authorize();
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();

        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

        Map<String, List<String>> entitiesFound = analyseEntity(languageServiceClient, doc);

        //device corpus
        List<String> deviceList = new LinkedList<>();
        deviceList.add("Samsung Galaxy S7");
        deviceList.add("Samsung GalaxyS7");
        deviceList.add("Iphone7");
        deviceList.add("IPhone6s");
        deviceList.add("GalaxyS8");
        //feature

        //main domain extraction map
        Map<String, List<String>> mainDomainExtraction = new LinkedHashMap<>();
        List<String> consumerGoodDomain = new LinkedList<>();
        List<String> workOfArtDomain = new LinkedList<>();
        List<String> organizationDomain = new LinkedList<>();
        List<String> locationDomain = new LinkedList<>();
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
                case "OTHER": otherDomain.add(entity);
                    break;
            }
        }
        //updating domain map
        mainDomainExtraction.put("CONSUMER_GOOD", consumerGoodDomain);
        mainDomainExtraction.put("WORK_OF_ART", workOfArtDomain);
        mainDomainExtraction.put("ORGANIZATION", organizationDomain);
        mainDomainExtraction.put("LOCATION", locationDomain);
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

    public static Map<String, List<String>> analyseEntity(LanguageServiceClient languageApi, Document doc) {
        Map<String, List<String>> entityList = new HashMap<>();

        AnalyzeEntitySentimentRequest request = AnalyzeEntitySentimentRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16).build();
        AnalyzeEntitySentimentResponse response = languageApi.analyzeEntitySentiment(request);

        System.out.println(" <------ GCP NLP Entity Sentiment Analysis -----> ");

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

        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16).build();
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

        return syntaxTagMap;

    }
    private static GoogleCredential authorize() throws IOException, GeneralSecurityException
    {
        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        return credential;
    }

}