package akura.cloundnlp;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.cloud.language.v1beta2.LanguageServiceClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * A snippet for Google Cloud Speech API showing how to analyze text message sentiment.
 */
public class Extractor {

    public static void main(String... args) throws Exception {
//        try (LanguageServiceClient languageServiceClient = LanguageServiceClient.create()) {
//            Document document = Document.newBuilder().build();
//            AnalyzeSentimentResponse response = languageServiceClient.analyzeSentiment(document);
//        }
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = authorize();
        Compute compute = new Compute.Builder(httpTransport, jsonFactory, credential).build();

        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();
    }

    private static GoogleCredential authorize() throws IOException, GeneralSecurityException
    {


        GoogleCredential credential = GoogleCredential.getApplicationDefault();

        return credential;

//        return new GoogleCredential.Builder()
//                .setTransport(HTTP_TRANSPORT)
//                .setJsonFactory(JSON_FACTORY)
//                .setServiceAccountId(serviceAccount)
//                .setServiceAccountScopes(SCOPES)
//                .setServiceAccountUser(serviceAccountUser)
//                // variable p12File is a String w/ path to the .p12 file name
//                .setServiceAccountPrivateKeyFromP12File(new java.io.File(p12File))
//                .build();
    }

}