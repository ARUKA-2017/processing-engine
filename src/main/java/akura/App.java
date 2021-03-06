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
import org.json.simple.JSONObject;
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

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", response);

            return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        });

        post("/modify-sentence", (req, res) -> {
            SentenceServiceResponse sentenceServiceResponse = gson.fromJson(req.body(), SentenceServiceResponse.class);
            String mainEntity = EntityExtractor.getMainSalienceEntity(sentenceServiceResponse.text);
            List<String> response = entityExtractorService.modifiedSentenceList(sentenceServiceResponse.text, mainEntity);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", response);

            return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        });

        get("/extract-review", (req, res) -> {
            CrawlerServiceResponse crawlerServiceResponse = new CrawlerServiceResponse();

            crawlerServiceResponse.searchKeyWord = req.queryParams("search");
            crawlerServiceResponse.url = req.queryParams("url");

            System.out.println("---------- Extract Review request recieved ----------------");
            System.out.println("searchKeyWord ---> "+ crawlerServiceResponse.searchKeyWord);
            System.out.println("url ---> "+ crawlerServiceResponse.url);
            System.out.println("------------------------------------------------------------");

//          gson.fromJson(req.body(), CrawlerServiceResponse.class);

            List<OntologyMapDto> ontologyMapDtos = execute.extractReviewOntologyMap(crawlerServiceResponse.url, crawlerServiceResponse.searchKeyWord);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", ontologyMapDtos);

            return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        });

        get("/get-entity", (req, res) -> {

            String entity = req.queryParams("entity");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", entityExtractorService.getEntity(entity));

            return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        });
    }
}

