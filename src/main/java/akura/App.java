package akura;

import akura.cloundnlp.EntityExtractor;
import akura.cloundnlp.dtos.OntologyMapDto;
import akura.crawler.Execute;
import akura.service.EntityExtractorService;
import akura.service.SparkMiddleware;
import akura.utility.CrawlerServiceResponse;
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
        Execute execute = new Execute();

        port(4568);
        Spark.staticFileLocation("/public");

        SparkMiddleware.enableCORS(
                "*",
                "POST, GET, OPTIONS, PUT, DELETE",
                "Content-Type, x-xsrf-token, content-Type, X-Auth-Token, Origin, Authorization"
        );

        post("/extract-entity", (req, res) -> {
            EntityServiceResponse entityServiceResponse = gson.fromJson(req.body(), EntityServiceResponse.class);
            String mainEntity = EntityExtractor.getMainSalienceEntity(entityServiceResponse.text);
            List<OntologyMapDto> response = entityExtractorService.extractEntity(entityServiceResponse.text, mainEntity);
            return new GsonBuilder().setPrettyPrinting().create().toJson(response);
        });

        post("/modify-sentence", (req, res) -> {
            SentenceServiceResponse sentenceServiceResponse = gson.fromJson(req.body(), SentenceServiceResponse.class);
            String mainEntity = EntityExtractor.getMainSalienceEntity(sentenceServiceResponse.text);
            List<String> response = entityExtractorService.modifiedSentenceList(sentenceServiceResponse.text, mainEntity);
            return new GsonBuilder().setPrettyPrinting().create().toJson(response);
        });

        post("/extract-review", (req, res) -> {
            CrawlerServiceResponse crawlerServiceResponse = gson.fromJson(req.body(), CrawlerServiceResponse.class);
            List<OntologyMapDto> ontologyMapDtos = execute.extractReviewOntologyMap(crawlerServiceResponse.url, crawlerServiceResponse.searchKeyWord);
            return new GsonBuilder().setPrettyPrinting().create().toJson(ontologyMapDtos);
        });
    }
}

