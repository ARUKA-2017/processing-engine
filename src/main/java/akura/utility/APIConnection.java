package akura.utility;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.NoSuchElementException;

/**
 * Class representing API Connection methods.
 */
public class APIConnection {
    private final static String DATA_GRAPH_URL = "http://concept.research.microsoft.com/api/Concept/ScoreByProb?instance=";


    public static void sendSocketRequest(String event, String msg) throws IOException {

        String urlParameters  = "event="+event+"&msg="+msg;
        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int    postDataLength = postData.length;
        String request        = "http://localhost:3000/broadcast";
        URL url            = new URL( request );
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        System.out.println("--------------- POST STARTED----------------");
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
            System.out.println("--------------- POST ----------------");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * MS graph connection
     *
     * @param text         - short text.
     * @param defaultValue - default value.
     * @return
     */
    public static String understandShortWordConcept(String text, String defaultValue) {
        String url = DATA_GRAPH_URL.concat(text.trim()).concat("&topK=1").replaceAll(" ", "%20");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse httpResponse = client.execute(getRequest);
            HttpEntity httpEntity = httpResponse.getEntity();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(EntityUtils.toString(httpEntity, "UTF-8"));
            return jsonObject.keySet().iterator().next().toString();
        } catch (IOException e) {
            System.out.println(e);
            return defaultValue;
        } catch (ParseException e) {
            System.out.println(e);
            return defaultValue;
        } catch (NoSuchElementException e) {
            System.out.println("Element not found!");
            return defaultValue;
        }
    }

    /**
     * Authenticate Google API
     *
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static GoogleCredential authorize() throws IOException, GeneralSecurityException {
        return GoogleCredential.getApplicationDefault();
    }

    /**
     * Provide Authentication with client.
     *
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static LanguageServiceClient provideLanguageServiceClient() throws IOException {
        try {
            authorize();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return LanguageServiceClient.create();
    }
}
