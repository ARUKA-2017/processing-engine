package akura;

import akura.cloundnlp.dtos.OntologyMapDto;
import akura.service.EntityExtractorService;
import akura.service.SparkMiddleware;
import akura.utility.EntityServiceResponse;
import akura.utility.SentenceServiceResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Spark;

import java.util.List;

import static spark.Spark.*;

/**
 * Application Startup
 */
public class App {

    public static void main(String[] args) {

        Gson gson = new Gson();
        EntityExtractorService entityExtractorService = new EntityExtractorService();

        port(4568);
        Spark.staticFileLocation("/public");

        SparkMiddleware.enableCORS(
                "*",
                "POST, GET, OPTIONS, PUT, DELETE",
                "Content-Type, x-xsrf-token, content-Type, X-Auth-Token, Origin, Authorization"
        );

        post("/extract-entity", (req, res) -> {
            EntityServiceResponse entityServiceResponse = gson.fromJson(req.body(), EntityServiceResponse.class);
            List<OntologyMapDto> response = entityExtractorService.extractEntity(entityServiceResponse.text, entityServiceResponse.entity);
            return new GsonBuilder().setPrettyPrinting().create().toJson(response);
        });

        post("/modify-sentence", (req, res) -> {
            SentenceServiceResponse sentenceServiceResponse = gson.fromJson(req.body(), SentenceServiceResponse.class);
            List<String> response = entityExtractorService.modifiedSentenceList(sentenceServiceResponse.text, sentenceServiceResponse.entity);
            return new GsonBuilder().setPrettyPrinting().create().toJson(response);
        });

    }
}

