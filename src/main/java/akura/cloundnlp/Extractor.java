package akura.cloundnlp;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.cloud.language.v1beta2.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * A snippet for Google Cloud Speech API showing how to analyze text message sentiment.
 */
public class Extractor {

    public static void main(String... args) throws Exception {
        GoogleCredential credential = authorize();
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();
        String text = "IPhone6s is better than Samsung GalaxyS8.";

        Document doc = Document.newBuilder()
                .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

        Map<String, List<String>> entitiesFound = analyseEntity(languageServiceClient, doc);
    }

    public static void analyseSentiment(LanguageServiceClient languageApi, Document doc) {

        // Detects the sentiment of the text
        Sentiment sentiment = languageApi.analyzeSentiment(doc).getDocumentSentiment();

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

    /**
     * Sample method to analyse syntax.
     * @param languageApi
     * @param doc
     */
    public static void analyseSyntax(LanguageServiceClient languageApi, Document doc) {

        AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16).build();
        AnalyzeSyntaxResponse response = languageApi.analyzeSyntax(request);

        System.out.println(" <------ GCP NLP Syntax Analysis -----> ");

        for(Token token : response.getTokensList())
        {
            System.out.println();
            System.out.println(token.toString());
            System.out.println();
        }

    }
    private static GoogleCredential authorize() throws IOException, GeneralSecurityException
    {
        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        return credential;
    }

}